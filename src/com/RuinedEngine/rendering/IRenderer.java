package com.RuinedEngine.rendering;

import com.RuinedEngine.core.Camera;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.lighting.DirectionalLight;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.lighting.SpotLight;

public interface IRenderer<T> {
	
	public void init() throws Exception;
	
	public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotlights, DirectionalLight directionalLight);
	
	abstract void bind(Model model);
	
	public void unbind();
	
	public void prepare(T t, Camera camera);
	
	public void cleanup();
}
