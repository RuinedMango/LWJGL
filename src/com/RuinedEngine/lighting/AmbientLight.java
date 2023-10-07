package com.RuinedEngine.lighting;

import org.joml.Vector3f;

public class AmbientLight {
	private Vector3f color;
	
	private float intensity;
	
	public AmbientLight(float intensity, Vector3f color) {
		this.intensity = intensity;
		this.color = color;
	}
	public AmbientLight() {
		this(1.0f, new Vector3f(1f,1f,1f));
	}
	public Vector3f getColor() {
		return color;
	}
	public void setColor(float r, float g, float b) {
		color.set(r,g,b);
	}
	public float getIntensity() {
		return intensity;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
}
