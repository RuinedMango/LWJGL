package com.RuinedEngine.core;

import com.RuinedEngine.rendering.GBuffer;
import com.RuinedEngine.rendering.GuiRender;
import com.RuinedEngine.rendering.LightsRender;
import com.RuinedEngine.rendering.SceneRender;
import com.RuinedEngine.rendering.ShadowRender;
import com.RuinedEngine.rendering.SkyBoxRender;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class Render {
	private GBuffer gBuffer;
	private SceneRender sceneRender;
	private LightsRender lightsRender;
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
		lightsRender = new LightsRender();
		gBuffer = new GBuffer(window);
	}
	public void cleanup() {
		sceneRender.cleanup();
		guiRender.cleanup();
		skyBoxRender.cleanup();
		shadowRender.cleanup();
		lightsRender.cleanup();
		gBuffer.cleanup();
	}
    private void lightRenderFinish() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void lightRenderStart(Window window) {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendEquation(GL14.GL_FUNC_ADD);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, gBuffer.getGBufferId());
    }

    public void render(Window window, Scene scene) {
        shadowRender.render(scene);
        sceneRender.render(scene, gBuffer);
        lightRenderStart(window);
        lightsRender.render(scene, shadowRender, gBuffer);
        skyBoxRender.render(scene);
        lightRenderFinish();
        guiRender.render(scene);
    }
	
	public void resize(int width, int height) {
		guiRender.resize(width, height);
	}
}
