package com.RuinedEngine.utils;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL11;

public class ArrTexture {
	private final int[] ids;
	
	public ArrTexture(int numTextures, int width, int height, int pixelFormat) {
		ids = new int[numTextures];
		GL11.glGenTextures(ids);
		
		for(int i = 0; i < numTextures; i++) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ids[i]);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, pixelFormat, GL11.GL_FLOAT, (ByteBuffer) null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		}
	}
	public void cleanup() {
		for(int id : ids) {
			GL11.glDeleteTextures(id);
		}
	}
	public int[] getIds() {
		return ids;
	}
}
