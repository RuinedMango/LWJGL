package com.RuinedEngine.VFX;

import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.ModelLoader;
import com.RuinedEngine.utils.TextureCache;

public class SkyBox {
	private Entity skyBoxEntity;
	private Model skyBoxModel;
	
	public SkyBox(String skyBoxModelPath, TextureCache textureCache) {
		skyBoxModel = ModelLoader.loadModel("skybox-model", skyBoxModelPath, textureCache);
		skyBoxEntity = new Entity("skyBoxEntity-entity", skyBoxModel.getId());
	}

	public Entity getSkyBoxEntity() {
		return skyBoxEntity;
	}

	public Model getSkyBoxModel() {
		return skyBoxModel;
	}
	
}
