package com.RuinedEngine.core;

import com.RuinedEngine.rendering.GuiRender;
import com.RuinedEngine.rendering.SceneRender;
import com.RuinedEngine.rendering.SkyBoxRender;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Render {
	private SceneRender sceneRender;
	private GuiRender guiRender;
	private SkyBoxRender skyBoxRender;
	public Render(Window window) {
		GL.createCapabilities();
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		sceneRender = new SceneRender();
		guiRender = new GuiRender(window);
		skyBoxRender = new SkyBoxRender();
	}
	public void cleanup() {
		sceneRender.cleanup();
		guiRender.cleanup();
		skyBoxRender.cleanup();
	}
	public void render(Window window, Scene scene) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		skyBoxRender.render(scene);
		sceneRender.render(scene);
		guiRender.render(scene);
	}
	
	public void resize(int width, int height) {
		guiRender.resize(width, height);
	}
}
