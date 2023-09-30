package com.RuinedEngine.entity;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class Texture {
	private int textureId;
	private String texturePath;
	
	public Texture(int width, int height, ByteBuffer buf) {
		this.texturePath = "";
		generateTexture(width, height, buf);
	}
	public Texture(String texturePath) {
		try(MemoryStack stack = MemoryStack.stackPush()){
			this.texturePath = texturePath;
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			ByteBuffer buf = STBImage.stbi_load(texturePath, w, h, channels, 4);
			if(buf == null) {
				throw new RuntimeException("Image File [" + texturePath + "] not loaded: " + STBImage.stbi_failure_reason());
			}
			int width = w.get();
			int height = h.get();
			
			generateTexture(width, height, buf);
			STBImage.stbi_image_free(buf);
		}	
	}
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}
	public void cleanup() {
		GL11.glDeleteTextures(textureId);
	}
	private void generateTexture(int width, int height, ByteBuffer buf) {
		textureId = GL11.glGenTextures();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
	}
	public String getTexturePath() {
		return texturePath;
	}
}
