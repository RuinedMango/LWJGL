package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.core.Scene;
import com.RuinedEngine.core.Window;
import com.RuinedEngine.utils.UniformsMap;

public class NKRender {
	private ShaderProgram shaderProgram;
	private UniformsMap uniformsMap;
	
	public NKRender(Window window) {
		List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/NK.vert", GL20.GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/NK.frag", GL20.GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		createUniforms();

	}
	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("Texture");
		uniformsMap.createUniform("ProjMTX");
	}
	public void render(Scene scene) {
		shaderProgram.bind();
		GL11.glEnable(GL11.GL_BLEND);
		GL30.glBlendEquation(GL30.GL_FUNC_ADD);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
	}
}
