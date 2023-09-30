package com.RuinedEngine.rendering;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;
import com.RuinedEngine.utils.Utils;

public class ShaderProgram {
	private final int programId;
	
	public ShaderProgram(List<ShaderModuleData> shaderModuleDataList) {
		programId = GL20.glCreateProgram();
		if(programId == 0) {
			throw new RuntimeException("Could not create shader");
		}
		List<Integer> shaderModules = new ArrayList<>();
		shaderModuleDataList.forEach(s -> shaderModules.add(createShader(Utils.readFile(s.shaderFile), s.shaderType)));
		link(shaderModules);
	}
	public void bind() {
		GL20.glUseProgram(programId);
	}
	public void cleanup() {
		unbind();
		if(programId != 0) {
			GL20.glDeleteProgram(programId);
		}
	}
	protected int createShader(String shaderCode, int shaderType) {
		int shaderId = GL20.glCreateShader(shaderType);
		if(shaderId == 0) {
			throw new RuntimeException("Error creating shader. Type: " + shaderType);
		}
		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);
		if(GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
			throw new RuntimeException("Error compiling shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
		}
		GL20.glAttachShader(programId, shaderId);
		
		return shaderId;
	}
	public int getProgramId() {
		return programId;
	}
	private void link(List<Integer> shaderModules) {
		GL20.glLinkProgram(programId);
		if(GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
			throw new RuntimeException("Error linking shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
		}
		shaderModules.forEach(s -> GL20.glDetachShader(programId, s));
		shaderModules.forEach(GL20::glDeleteShader);
	}
	public void unbind() {
		GL20.glUseProgram(0);
	}
	public void validate() {
		GL20.glValidateProgram(programId);
		if(GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
			throw new RuntimeException("Error validating shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
		}
	}
	public record ShaderModuleData(String shaderFile, int shaderType) {
		
	}
}
