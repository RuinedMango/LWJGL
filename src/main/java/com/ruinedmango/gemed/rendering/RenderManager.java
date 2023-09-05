package main.java.com.ruinedmango.gemed.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import main.java.com.ruinedmango.gemed.core.Camera;
import main.java.com.ruinedmango.gemed.core.ShaderManager;
import main.java.com.ruinedmango.gemed.core.WindowManager;
import main.java.com.ruinedmango.gemed.core.entity.Entity;
import main.java.com.ruinedmango.gemed.core.entity.Model;
import main.java.com.ruinedmango.gemed.core.entity.terrain.Terrain;
import main.java.com.ruinedmango.gemed.core.utils.Consts;
import main.java.com.ruinedmango.gemed.core.utils.Transformation;
import main.java.com.ruinedmango.gemed.core.utils.Utils;
import main.java.com.ruinedmango.gemed.lighting.DirectionalLight;
import main.java.com.ruinedmango.gemed.lighting.PointLight;
import main.java.com.ruinedmango.gemed.lighting.SpotLight;
import main.java.com.ruinedmango.gemed.test.Launcher;



public class RenderManager {
	private final WindowManager window;
	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;
	
	
	public RenderManager() {
		window = Launcher.getWindow();
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
	
	public void render(Camera camera,DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
		clear();
		
		entityRenderer.render(camera, pointLights, spotLights, directionalLight);
		terrainRenderer.render(camera, pointLights, spotLights, directionalLight);
		
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
