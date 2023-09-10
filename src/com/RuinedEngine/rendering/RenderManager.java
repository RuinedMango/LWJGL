package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

import com.RuinedEngine.core.Camera;
import com.RuinedEngine.core.ShaderManager;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.SceneManager;
import com.RuinedEngine.entity.terrain.Terrain;
import com.RuinedEngine.lighting.DirectionalLight;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.lighting.SpotLight;
import com.RuinedEngine.test.Launcher;
import com.RuinedEngine.utils.Consts;



public class RenderManager {
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	
	private static boolean isCulling = false;
	
	public RenderManager() {
		Launcher.getWindow();
	}
	
	public void init() throws Exception {
		entityRenderer = new EntityRenderer();
		terrainRenderer = new TerrainRenderer();
		entityRenderer.init();
		terrainRenderer.init();
	}
	
	public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, ShaderManager shader) {
		shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
		shader.setUniform("specularPower", Consts.SPECULAR_POWER);
		int numLights = spotLights != null ? spotLights.length : 0;
		for(int i = 0; i < numLights; i++) {
			shader.setUniform("spotLights", spotLights[i], i);
		}
		numLights = pointLights != null ? pointLights.length : 0;
		for(int i = 0; i < numLights; i++) {
			shader.setUniform("pointLights", pointLights[i], i);
		}
		shader.setUniform("directionalLight", directionalLight);
	}
	
	public static void enableCulling() {
		if(!isCulling) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			isCulling = true;
		}
	}
	
	public static void disableCulling() {
		if(isCulling) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			isCulling = false;
		}
	}
	
	public void render(Camera camera,SceneManager sceneManager) {
		clear();
		
		entityRenderer.render(camera, sceneManager.getPointLights(), sceneManager.getSpotLights(), sceneManager.getDirectionalLight());
		terrainRenderer.render(camera, sceneManager.getPointLights(), sceneManager.getSpotLights(), sceneManager.getDirectionalLight());
	}
	public void processEntity(Entity entity) {
		List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
		if(entityList != null) {
			entityList.add(entity);
		}else {
			List<Entity> newEntityList = new ArrayList<>();
			newEntityList.add(entity);
			entityRenderer.getEntities().put(entity.getModel(), newEntityList);
		}
	}
	public void processTerrain(Terrain terrain) {
		terrainRenderer.getTerrains().add(terrain);
	}
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	public void cleanup() {
		entityRenderer.cleanup();
		terrainRenderer.cleanup();
	}
}
