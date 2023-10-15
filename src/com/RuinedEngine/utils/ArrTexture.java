package com.RuinedEngine.utils;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL30;

public class ArrTexture {

    private final int[] ids;

    public ArrTexture(int numTextures, int width, int height, int pixelFormat) {
        ids = new int[numTextures];
        GL30.glGenTextures(ids);

        for (int i = 0; i < numTextures; i++) {
        	GL30.glBindTexture(GL30.GL_TEXTURE_2D, ids[i]);
        	GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT, width, height, 0, pixelFormat, GL30.GL_FLOAT, (ByteBuffer) null);
        	GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        	GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        	GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_MODE, GL30.GL_NONE);
        	GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        	GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        }
    }

    public void cleanup() {
        for (int id : ids) {
        	GL30.glDeleteTextures(id);
        }
    }

    public int[] getIds() {
        return ids;
    }
}
