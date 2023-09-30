package com.RuinedEngine.GUI;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import imgui.ImDrawData;

public class GuiMesh {
	private int indicesVBO;
	private int vaoId;
	private int verticesVBO;
	public GuiMesh() {
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		
		verticesVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verticesVBO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1,2,GL11.GL_FLOAT,false,ImDrawData.SIZEOF_IM_DRAW_VERT, 8);
		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, 4,GL11.GL_UNSIGNED_BYTE, true, ImDrawData.SIZEOF_IM_DRAW_VERT, 16);
		indicesVBO = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	public void cleanup() {
		GL15.glDeleteBuffers(indicesVBO);
		GL15.glDeleteBuffers(verticesVBO);
		GL30.glDeleteVertexArrays(vaoId);
	}
	public int getIndicesVBO() {
		return indicesVBO;
	}
	public int getVaoId() {
		return vaoId;
	}
	public int getVerticesVBO() {
		return verticesVBO;
	}
}
