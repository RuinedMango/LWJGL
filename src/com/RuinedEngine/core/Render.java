package com.RuinedEngine.core;

import com.RuinedEngine.rendering.GuiRender;
import com.RuinedEngine.rendering.SceneRender;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Render {
	private SceneRender sceneRender;
	private GuiRender guiRender;
	public Render(Window window) {
		GL.createCapabilities();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		sceneRender = new SceneRender();
		guiRender = new GuiRender(window);
	}
	public void cleanup() {
		sceneRender.cleanup();
		guiRender.cleanup();
	}
	public void render(Window window, Scene scene) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		sceneRender.render(scene);
		guiRender.render(scene);
	}
	
	public void resize(int width, int height) {
		guiRender.resize(width, height);
	}
}
