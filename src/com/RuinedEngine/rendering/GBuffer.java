package com.RuinedEngine.rendering;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.RuinedEngine.core.Window;

public class GBuffer {
	private static final int TOTAL_TEXTURES = 4;
	
	private int gBufferId;
	private int height;
	private int[] textureIds;
	private int width;
	
	public GBuffer(Window window) {
		gBufferId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, gBufferId);
		
		textureIds = new int[TOTAL_TEXTURES];
		GL11.glGenTextures(textureIds);
		
		this.width = window.getWidth();
		this.height = window.getHeight();
		
		for(int  i = 0; i < TOTAL_TEXTURES; i++) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[i]);
			int attachmentType;
			if(i == TOTAL_TEXTURES - 1) {
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT,(ByteBuffer) null);
				attachmentType = GL30.GL_DEPTH_ATTACHMENT;
			}else {
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT,(ByteBuffer) null);
				attachmentType = GL30.GL_COLOR_ATTACHMENT0 + i;
			}
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachmentType, GL11.GL_TEXTURE_2D, textureIds[i], 0);
		}
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer intBuff = stack.mallocInt(TOTAL_TEXTURES);
			for(int i = 0; i < TOTAL_TEXTURES; i++) {
				intBuff.put(i, GL30.GL_COLOR_ATTACHMENT0 + i);
			}
			GL20.glDrawBuffers(intBuff);
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	public void cleanup() {
		GL30.glDeleteFramebuffers(gBufferId);
		Arrays.stream(textureIds).forEach(GL30::glDeleteTextures);
	}
	public int getGBufferId() {
		return gBufferId;
	}
	public int getHeight() {
		return height;
	}
	public int[] getTextureIds() {
		return textureIds;
	}
	public int getWidth() {
		return width;
	}
}
