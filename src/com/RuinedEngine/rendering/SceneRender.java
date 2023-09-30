package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.core.Scene;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Material;
import com.RuinedEngine.entity.Mesh;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.Texture;
import com.RuinedEngine.utils.TextureCache;
import com.RuinedEngine.utils.UniformsMap;

public class SceneRender {
	private ShaderProgram shaderProgram;
	private UniformsMap uniformsMap;
	
	public SceneRender() {
		List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.vert", GL20.GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/scene.frag", GL20.GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		createUniforms();
	}
	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("projectionMatrix");
		uniformsMap.createUniform("viewMatrix");
		uniformsMap.createUniform("modelMatrix");
		uniformsMap.createUniform("txtSampler");
		uniformsMap.createUniform("material.diffuse");
	}
	public void cleanup() {
		shaderProgram.cleanup();
	}
	public void render(Scene scene) {
		shaderProgram.bind();
		
		uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
		uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
		uniformsMap.setUniform("txtSampler", 0);
		
		Collection<Model> models = scene.getModelMap().values();
		TextureCache textureCache = scene.getTextureCache();
		for(Model model : models) {
			List<Entity> entities = model.getEntitiesList();
			
			for(Material material : model.getMaterialList()) {
				uniformsMap.setUniform("material.diffuse", material.getDiffuseColor());
				Texture texture = textureCache.getTexture(material.getTexturePath());
				GL13.glActiveTexture(GL11.GL_TEXTURE);
				texture.bind();
				
				for(Mesh mesh : material.getMeshList()) {
					GL30.glBindVertexArray(mesh.getVaoId());
					for(Entity entity : entities) {
						uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
						GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getNumVertices(), GL11.GL_UNSIGNED_INT, 0);
					}
				}
			}
		}
		
		GL30.glBindVertexArray(0);
		shaderProgram.unbind();
	}
}
