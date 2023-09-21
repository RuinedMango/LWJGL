package com.RuinedEngine.rendering;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL11;

import com.RuinedEngine.core.Scene;
import com.RuinedEngine.core.Window;

public class Render {
	public Render() {
		GL.createCapabilities();
	}
	public void cleanup() {
		
	}
	public void render(Window window, Scene scene) {
		GL11.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
	}
}
