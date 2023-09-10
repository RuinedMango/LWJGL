package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.core.Camera;
import com.RuinedEngine.core.ShaderManager;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.terrain.Terrain;
import com.RuinedEngine.lighting.DirectionalLight;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.lighting.SpotLight;
import com.RuinedEngine.test.Launcher;
import com.RuinedEngine.utils.Consts;
import com.RuinedEngine.utils.Transformation;
import com.RuinedEngine.utils.Utils;

public class TerrainRenderer implements IRenderer<Object>{
	
	ShaderManager shader;
	private List<Terrain> terrains;
	
	public TerrainRenderer() throws Exception{
		terrains  = new ArrayList<>();
		shader = new ShaderManager();
	}
	
	@Override
	public void init() throws Exception {
		shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vsh"));
		shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.fsh"));
		shader.link();
		shader.createUniform("backgroundTexture");
		shader.createUniform("redTexture");
		shader.createUniform("greenTexture");
		shader.createUniform("blueTexture");
		shader.createUniform("blendMap");
		shader.createUniform("transformationMatrix");
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("ambientLight");
		shader.createMaterialUniform("material");
		shader.createUniform("specularPower");
		shader.createDirectionalLightUniform("directionalLight");
		shader.createPointLightListUniform("pointLights", Consts.MAX_POINT_LIGHTS);
		shader.createSpotLightListUniform("spotLights", Consts.MAX_SPOT_LIGHTS);
	}

	@Override
	public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
			DirectionalLight directionalLight) {
		shader.bind();
		shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix());
		RenderManager.renderLights(pointLights,spotLights,directionalLight,shader);
		for(Terrain terrain : terrains) {
			bind(terrain.getModel());

			prepare(terrain, camera);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbind();
		}
		terrains.clear();
		shader.unbind();
	}

	@Override
	public void bind(Model model) {
		GL30.glBindVertexArray(model.getId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		RenderManager.enableCulling();
		
		shader.setUniform("backgroundTexture", 0);
		shader.setUniform("redTexture", 1);
		shader.setUniform("greenTexture", 2);
		shader.setUniform("blueTexture", 3);
		shader.setUniform("blendMap", 4);
		shader.setUniform("material", model.getMaterial());
	}

	@Override
	public void unbind() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void prepare(Object terrain, Camera camera) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getBackground().getId());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getRedTexture().getId());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getGreenTexture().getId());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMapTerrain().getBlueTexture().getId());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((Terrain)terrain).getBlendMap().getId());
		
		shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Terrain) terrain));
		shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
	}

	@Override
	public void cleanup() {
		shader.cleanup();
	}

	public List<Terrain> getTerrains() {
		return terrains;
	}
	
}
