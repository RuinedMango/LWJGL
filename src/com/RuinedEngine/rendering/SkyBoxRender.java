package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.VFX.SkyBox;
import com.RuinedEngine.core.Scene;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Material;
import com.RuinedEngine.entity.Mesh;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.Texture;
import com.RuinedEngine.utils.TextureCache;
import com.RuinedEngine.utils.UniformsMap;



public class SkyBoxRender {
	private ShaderProgram shaderProgram;
	private UniformsMap uniformsMap;
	private Matrix4f viewMatrix;
	
	public SkyBoxRender() {
		List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/skybox.vert", GL20.GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/skybox.frag", GL20.GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		viewMatrix = new Matrix4f();
		createUniforms();
	}
	
	public void cleanup() {
		shaderProgram.cleanup();
	}
	
	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("projectionMatrix");
		uniformsMap.createUniform("viewMatrix");
		uniformsMap.createUniform("modelMatrix");
		uniformsMap.createUniform("diffuse");
		uniformsMap.createUniform("txtSampler");
		uniformsMap.createUniform("hasTexture");
	}
	
	public void render(Scene scene) {
		SkyBox skyBox = scene.getSkyBox();
		if(skyBox == null) {
			return;
		}
		shaderProgram.bind();
		
		uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
		viewMatrix.set(scene.getCamera().getViewMatrix());
		viewMatrix.m30(0);
		viewMatrix.m31(0);
		viewMatrix.m32(0);
		uniformsMap.setUniform("viewMatrix", viewMatrix);
		uniformsMap.setUniform("txtSampler", 0);
		
		Model skyBoxModel = skyBox.getSkyBoxModel();
		Entity skyBoxEntity = skyBox.getSkyBoxEntity();
		TextureCache textureCache = scene.getTextureCache();
		for(Material material : skyBoxModel.getMaterialList()) {
			Texture texture = textureCache.getTexture(material.getTexturePath());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			texture.bind();
			
			uniformsMap.setUniform("diffuse", material.getDiffuseColor());
			uniformsMap.setUniform("hasTexture", texture.getTexturePath().equals(TextureCache.DEFAULT_TEXTURE) ? 0 : 1);
			
			for (Mesh mesh : material.getMeshList()) {
				GL30.glBindVertexArray(mesh.getVaoId());
				uniformsMap.setUniform("modelMatrix", skyBoxEntity.getModelMatrix());
				GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getNumVertices(), GL11.GL_UNSIGNED_INT, 0);
			}
			
		}
		GL30.glBindVertexArray(0);
		
		shaderProgram.unbind();
	}
	
}
