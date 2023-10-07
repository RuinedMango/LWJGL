package com.RuinedEngine.entity;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import com.RuinedEngine.utils.Consts;

public class Material {
	private List<Mesh> meshList;
	private String texturePath;
	private Vector4f ambientColor;
	private Vector4f diffuseColor;
	private float reflectance;
	private Vector4f specularColor;
	
	public Material() {
		diffuseColor = Consts.DEFAULT_COLOR;
		ambientColor = Consts.DEFAULT_COLOR;
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
	public Vector4f getAmbientColor() {
		return ambientColor;
	}
	public void setAmbientColor(Vector4f ambientColor) {
		this.ambientColor = ambientColor;
	}
	public float getReflectance() {
		return reflectance;
	}
	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}
	public Vector4f getSpecularColor() {
		return specularColor;
	}
	public void setSpecularColor(Vector4f specularColor) {
		this.specularColor = specularColor;
	}
	public void setMeshList(List<Mesh> meshList) {
		this.meshList = meshList;
	}
	
}
