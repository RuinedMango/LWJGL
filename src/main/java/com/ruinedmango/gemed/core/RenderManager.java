package main.java.com.ruinedmango.gemed.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import main.java.com.ruinedmango.gemed.core.entity.Entity;
import main.java.com.ruinedmango.gemed.core.utils.Consts;
import main.java.com.ruinedmango.gemed.core.utils.Transformation;
import main.java.com.ruinedmango.gemed.core.utils.Utils;
import main.java.com.ruinedmango.gemed.lighting.DirectionalLight;
import main.java.com.ruinedmango.gemed.lighting.PointLight;
import main.java.com.ruinedmango.gemed.lighting.SpotLight;
import main.java.com.ruinedmango.gemed.test.Launcher;



public class RenderManager {
	private final WindowManager window;
	private ShaderManager shader;
	
	public RenderManager() {
		window = Launcher.getWindow();
	}
	
	public void init() throws Exception {
		shader = new ShaderManager();
		shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
		shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
		shader.link();
		shader.createUniform("textureSampler");
		shader.createUniform("transformationMatrix");
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("ambientLight");
		shader.createMaterialUniform("material");
		shader.createUniform("specularPower");
		shader.createDirectionalLightUniform("directionalLight");
		shader.createPointLightListUniform("pointLights", 5);
		shader.createSpotLightListUniform("spotLights", 5);
	}
	public void render(Entity entity,Camera camera,DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
		clear();
		shader.bind();
		shader.setUniform("textureSampler", 0);
		shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
		shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
		shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
		shader.setUniform("material", entity.getModel().getMaterial());
		shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
		shader.setUniform("specularPower", Consts.SPECULAR_POWER);
		shader.setUniform("directionalLight", directionalLight);
		
		int numLights = spotLights != null ? spotLights.length : 0;
		for(int i = 0; i < numLights; i++) {
			shader.setUniform("spotLights", spotLights[i], i);
		}
		numLights = pointLights != null ? pointLights.length : 0;
		for(int i = 0; i < numLights; i++) {
			shader.setUniform("pointLights", pointLights[i], i);
		}
		GL30.glBindVertexArray(entity.getModel().getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getModel().getTexture().getId());
		GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		shader.unbind();
	}
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	public void cleanup() {
		shader.cleanup();
	}
}
