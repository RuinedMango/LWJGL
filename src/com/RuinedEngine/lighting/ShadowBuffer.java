package com.RuinedEngine.lighting;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.utils.ArrTexture;

public class ShadowBuffer {
	public static final int SHADOW_MAP_WIDTH = 4096;
	
	public static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
	private final ArrTexture depthMap;
	private final int depthMapFBO;
	
	public ShadowBuffer() {
		depthMapFBO = GL30.glGenFramebuffers();
		
		depthMap = new ArrTexture(CascadeShadow.SHADOW_MAP_CASCADE_COUNT, SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL11.GL_DEPTH_COMPONENT);
		
		GL20.glBindBuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap.getIds()[0], 0);
		
		GL30.glDrawBuffer(GL11.GL_NONE);
		GL30.glReadBuffer(GL11.GL_NONE);
		
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Could not create FrameBuffer");
		}
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	public void bindTextures(int start) {
		for(int i = 0; i < CascadeShadow.SHADOW_MAP_CASCADE_COUNT; i++) {
			GL30.glActiveTexture(start + i);
			GL30.glBindTexture(GL20.GL_TEXTURE_2D, depthMap.getIds()[i]);
		}
	}
	public void cleanup() {
		GL30.glDeleteFramebuffers(depthMapFBO);
		depthMap.cleanup();
	}
	public int getDepthMapFBO() {
		return depthMapFBO;
	}
	public ArrTexture getDepthMapTexture() {
		return depthMap;
	}
}
