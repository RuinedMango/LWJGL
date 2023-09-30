package com.RuinedEngine.entity;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL30.glGenBuffers;

public class Mesh {
	private int numVertices;
	private int vaoId;
	private List<Integer> vboIdList;
	
	public Mesh(float[] positions,float[] textCoords, int[] indices) {
		try(MemoryStack stack = MemoryStack.stackPush()){
			this.numVertices = indices.length;
			vboIdList = new ArrayList<>();
			
			vaoId = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoId);
			
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
			positionsBuffer.put(0, positions);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionsBuffer, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
			
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer textCoordsBuffer = stack.callocFloat(textCoords.length);
			textCoordsBuffer.put(0, textCoords);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
			
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			IntBuffer indicesBuffer = stack.callocInt(indices.length);
			indicesBuffer.put(0, indices);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL30.glBindVertexArray(0);
		}
	}
	public void cleanup() {
		vboIdList.forEach(GL30::glDeleteBuffers);
		GL30.glDeleteVertexArrays(vaoId);
	}
	public int getNumVertices() {
		return numVertices;
	}
	public final int getVaoId() {
		return vaoId;
	}
}
