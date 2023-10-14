package com.RuinedEngine.core;

import com.RuinedEngine.rendering.GuiRender;
import com.RuinedEngine.rendering.SceneRender;
import com.RuinedEngine.rendering.ShadowRender;
import com.RuinedEngine.rendering.SkyBoxRender;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Render {
	private SceneRender sceneRender;
	private GuiRender guiRender;
	private SkyBoxRender skyBoxRender;
	private ShadowRender shadowRender;
	public Render(Window window) {
		GL.createCapabilities();
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		sceneRender = new SceneRender();
		guiRender = new GuiRender(window);
		skyBoxRender = new SkyBoxRender();
		shadowRender = new ShadowRender();
	}
	public void cleanup() {
		sceneRender.cleanup();
		guiRender.cleanup();
		skyBoxRender.cleanup();
		shadowRender.cleanup();
	}
	public void render(Window window, Scene scene) {
		shadowRender.render(scene);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		skyBoxRender.render(scene);
		sceneRender.render(scene, shadowRender);
		guiRender.render(scene);
	}
	
	public void resize(int width, int height) {
		guiRender.resize(width, height);
	}
}
