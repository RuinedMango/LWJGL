package main.java.com.ruinedmango.gemed.rendering;

import main.java.com.ruinedmango.gemed.core.Camera;
import main.java.com.ruinedmango.gemed.core.entity.Model;
import main.java.com.ruinedmango.gemed.lighting.DirectionalLight;
import main.java.com.ruinedmango.gemed.lighting.PointLight;
import main.java.com.ruinedmango.gemed.lighting.SpotLight;

public interface IRenderer<T> {
	
	public void init() throws Exception;
	
	public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotlights, DirectionalLight directionalLight);
	
	abstract void bind(Model model);
	
	public void unbind();
	
	public void prepare(T t, Camera camera);
	
	public void cleanup();
}
