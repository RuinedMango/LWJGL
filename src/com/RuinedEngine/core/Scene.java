package com.RuinedEngine.core;

import java.util.HashMap;
import java.util.Map;

import com.RuinedEngine.GUI.IGuiInstance;
import com.RuinedEngine.VFX.Fog;
import com.RuinedEngine.VFX.SkyBox;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.lighting.SceneLights;
import com.RuinedEngine.utils.Projection;
import com.RuinedEngine.utils.TextureCache;

public class Scene {
	private Map<String, Model> modelMap;
	private Projection projection;
	private TextureCache textureCache;
	private Camera camera;
	private SkyBox skyBox;
	private Fog fog;
	private IGuiInstance guiInstance;
	private SceneLights sceneLights;
	public Scene( int width, int height) {
		modelMap = new HashMap<>();
		projection = new Projection(width, height);
		textureCache = new TextureCache();
		camera = new Camera();
		fog = new Fog();
	}
	
	public void addEntity(Entity entity) {
		String modelId = entity.getModelId();
		Model model = modelMap.get(modelId);
		if(model == null) {
			throw new RuntimeException("Could not find model [" + modelId + "]");
		}
		model.getEntitiesList().add(entity);
	}
	public void addModel(Model model) {
		modelMap.put(model.getId(), model);
	}
	public void cleanup() {
		modelMap.values().forEach(Model::cleanup);
	}
	public Map<String, Model> getModelMap(){
		return modelMap;
	}
	public Camera getCamera() {
		return camera;
	}
	public SceneLights getSceneLights() {
		return sceneLights;
	}
	public Projection getProjection() {
		return projection;
	}
	public IGuiInstance getGuiInstance() {
		return guiInstance;
	}
	public SkyBox getSkyBox() {
		return skyBox;
	}
	public TextureCache getTextureCache() {
		return textureCache;
	}
	public void setGuiInstance(IGuiInstance guiInstance) {
		this.guiInstance = guiInstance;
	}
	public void setSceneLights(SceneLights sceneLights) {
		this.sceneLights = sceneLights;
	}
	public void setSkyBox(SkyBox skyBox) {
		this.skyBox = skyBox;
	}
	public Fog getFog() {
		return fog;
	}
	public void setFog(Fog fog) {
		this.fog = fog;
	}
	public void resize(int width, int height) {
		projection.updateProjMatrix(width, height);
	}
}
