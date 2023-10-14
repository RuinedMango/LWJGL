package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.core.Scene;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Material;
import com.RuinedEngine.entity.Mesh;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.lighting.CascadeShadow;
import com.RuinedEngine.lighting.ShadowBuffer;
import com.RuinedEngine.utils.AnimationData;
import com.RuinedEngine.utils.UniformsMap;

public class ShadowRender {
	private ArrayList<CascadeShadow> cascadeShadows;
	private ShaderProgram shaderProgram;
	private ShadowBuffer shadowBuffer;
	private UniformsMap uniformsMap;
	
	public ShadowRender() {
		List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/shadow.vert", GL30.GL_VERTEX_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		
		shadowBuffer = new ShadowBuffer();
		
		cascadeShadows = new ArrayList<>();
		for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++) {
			CascadeShadow cascadeShadow = new CascadeShadow();
			cascadeShadows.add(cascadeShadow);
		}
		createUniforms();
	}
	public void cleanup() {
		shaderProgram.cleanup();
		shadowBuffer.cleanup();
	}
	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("modelMatrix");
		uniformsMap.createUniform("projViewMatrix");
		uniformsMap.createUniform("bonesMatrices");
	}
	public List<CascadeShadow> getCascadeShadow(){
		return cascadeShadows;
	}
	public ShadowBuffer getShadowBuffer() {
		return shadowBuffer;
	}
	public void render(Scene scene) {
		CascadeShadow.updateCascadeShadows(cascadeShadows, scene);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowBuffer.getDepthMapFBO());
		GL30.glViewport(0, 0, ShadowBuffer.SHADOW_MAP_WIDTH, ShadowBuffer.SHADOW_MAP_HEIGHT);
		
		shaderProgram.bind();
		
		Collection<Model> models = scene.getModelMap().values();
		for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, shadowBuffer.getDepthMapTexture().getIds()[i], 0);
			GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
			
			CascadeShadow shadowCascade = cascadeShadows.get(i);
			uniformsMap.setUniform("projViewMatrix", shadowCascade.getProjViewMatrix());
			
			for(Model model : models) {
				List<Entity> entities = model.getEntitiesList();
				for(Material material : model.getMaterialList()) {
					for(Mesh mesh : material.getMeshList()) {
						GL30.glBindVertexArray(mesh.getVaoId());
						for(Entity entity : entities) {
							uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
							AnimationData animationData = entity.getAnimationData();
							if(animationData == null) {
								uniformsMap.setUniform("bonesMatrices", AnimationData.DEFAULT_BONES_MATRICES);
							}else {
								uniformsMap.setUniform("bonesMatrices", animationData.getCurrentFrame().boneMatrices());
							}
							GL30.glDrawElements(GL11.GL_TRIANGLES, mesh.getNumVertices(), GL11.GL_UNSIGNED_INT, 0);
						}
					}
				}
			}
		}
		shaderProgram.unbind();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
}
