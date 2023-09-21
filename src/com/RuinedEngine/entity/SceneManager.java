package com.RuinedEngine.entity;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.RuinedEngine.VFX.IParticleEmitter;
import com.RuinedEngine.entity.terrain.Terrain;
import com.RuinedEngine.lighting.DirectionalLight;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.lighting.SpotLight;
import com.RuinedEngine.utils.Consts;

public class SceneManager {
	
	private List<Entity> entities;
	private List<Terrain> terrains;
	
	private Vector3f ambientLight;
	private SpotLight[] spotLights;
	private PointLight[] pointLights;
	private IParticleEmitter[] particleEmitters;
	private DirectionalLight directionalLight;
	private float lightAngle;
	private float spotAngle = 0;
	private float spotInc = 1;
	
	public SceneManager(float lightAngle) {
		entities = new ArrayList<>();
		terrains = new ArrayList<>();
		ambientLight = Consts.AMBIENT_LIGHT;
		this.lightAngle = lightAngle;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public List<Terrain> getTerrains() {
		return terrains;
	}

	public void setTerrains(List<Terrain> terrains) {
		this.terrains = terrains;
	}
	
	public void addTerrain(Terrain terrain) {
		this.terrains.add(terrain);
	}

	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	public void setAmbientLight(Vector3f ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public void setAmbientLight(float x, float y, float z) {
		ambientLight = new Vector3f(x,y,z);
	}

	public SpotLight[] getSpotLights() {
		return spotLights;
	}

	public void setSpotLights(SpotLight[] spotLights) {
		this.spotLights = spotLights;
	}

	public PointLight[] getPointLights() {
		return pointLights;
	}

	public void setPointLights(PointLight[] pointLights) {
		this.pointLights = pointLights;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public void setDirectionalLight(DirectionalLight directionalLight) {
		this.directionalLight = directionalLight;
	}

	public float getLightAngle() {
		return lightAngle;
	}

	public void setLightAngle(float lightAngle) {
		this.lightAngle = lightAngle;
	}

	public float getSpotAngle() {
		return spotAngle;
	}

	public void setSpotAngle(float spotAngle) {
		this.spotAngle = spotAngle;
	}

	public float getSpotInc() {
		return spotInc;
	}

	public void setSpotInc(float spotInc) {
		this.spotInc = spotInc;
	}

	public IParticleEmitter[] getParticleEmitters() {
		return particleEmitters;
	}

	public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
		this.particleEmitters = particleEmitters;
	}
	
}
