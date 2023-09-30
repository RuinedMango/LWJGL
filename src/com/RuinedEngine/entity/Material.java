package com.RuinedEngine.entity;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.RuinedEngine.utils.Consts;

public class Material {
	private List<Mesh> meshList;
	private String texturePath;
	private Vector4f diffuseColor;
	
	public Material() {
		diffuseColor = Consts.DEFAULT_COLOR;
		meshList = new ArrayList<>();
	}
	public void cleanup() {
		meshList.forEach(Mesh::cleanup);
	}
	public List<Mesh> getMeshList(){
		return meshList;
	}
	public Vector4f getDiffuseColor() {
		return diffuseColor;
	}
	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}
	public String getTexturePath() {
		return texturePath;
	}
	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}
}
