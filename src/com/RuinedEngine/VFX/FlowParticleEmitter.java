package com.RuinedEngine.VFX;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Vector3f;

import com.RuinedEngine.entity.Entity;

public class FlowParticleEmitter implements IParticleEmitter{

	private int maxParticles;
	
	private boolean active;
	
	private final List<Entity> particles;
	
	private final Particle baseParticle;
	
	private long creationPeriodMillis;
	
	private long lastCreationTime;
	
	private float speedRndRange;
	
	private float positionRndRange;
	
	private float scaleRndRange;
	
	public FlowParticleEmitter(Particle baseParticle, int maxParticles, long creationPeriodMillis) {
		particles = new ArrayList<>();
		this.baseParticle = baseParticle;
		this.maxParticles = maxParticles;
		this.active = false;
		this.lastCreationTime = 0;
		this.creationPeriodMillis = creationPeriodMillis;
	}
	@Override
	public void cleanup() {

	}

	@Override
	public Particle getBaseParticle() {
		return baseParticle;
	}

	@Override
	public List<Entity> getParticles() {
		return particles;
	}
	public int getMaxParticles() {
		return maxParticles;
	}
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public long getCreationPeriodMillis() {
		return creationPeriodMillis;
	}
	public void setCreationPeriodMillis(long creationPeriodMillis) {
		this.creationPeriodMillis = creationPeriodMillis;
	}
	public long getLastCreationTime() {
		return lastCreationTime;
	}
	public void setLastCreationTime(long lastCreationTime) {
		this.lastCreationTime = lastCreationTime;
	}
	public float getSpeedRndRange() {
		return speedRndRange;
	}
	public void setSpeedRndRange(float speedRndRange) {
		this.speedRndRange = speedRndRange;
	}
	public float getPositionRndRange() {
		return positionRndRange;
	}
	public void setPositionRndRange(float positionRndRange) {
		this.positionRndRange = positionRndRange;
	}
	public float getScaleRndRange() {
		return scaleRndRange;
	}
	public void setScaleRndRange(float scaleRndRange) {
		this.scaleRndRange = scaleRndRange;
	}
	public void update(long ellapsedTime) {
		long now = System.currentTimeMillis();
		if(lastCreationTime == 0) {
			lastCreationTime = now;
		}
		Iterator<? extends Entity> it = particles.iterator();
		while(it.hasNext()) {
			Particle particle = (Particle) it.next();
			if(particle.updateTtl(ellapsedTime) < 0) {
				it.remove();
			}else {
				updatePosition(particle, ellapsedTime);
			}
		}
		int length = this.getParticles().size();
		if(now - lastCreationTime >= this.creationPeriodMillis && length < maxParticles) {
			createParticle();
			this.lastCreationTime = now;
		}
	}
	public void createParticle() {
		Particle particle = new Particle(this.getBaseParticle());
		
		float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
		float speedInc = sign * (float)Math.random() * this.speedRndRange;
		float posInc = sign * (float)Math.random() * this.positionRndRange;
		float scaleInc = sign * (float)Math.random() * this.scaleRndRange;
		particle.getPos().add(posInc, posInc, posInc);
		particle.getSpeed().add(speedInc, speedInc, speedInc);
		particle.setScale(particle.getScale() + scaleInc);
		particles.add(particle);
	}
	
	/**
	 * Updates a particle position
	 * @param particle The particle to update
	 * @param elapsedTime Elapsed time in milliseconds
	 */
	public void updatePosition(Particle particle, long elapsedTime) {
		Vector3f speed = particle.getSpeed();
		float delta = elapsedTime / 1000.0f;
		float dx = speed.x * delta;
		float dy = speed.y * delta;
		float dz = speed.z * delta;
		Vector3f pos = particle.getPos();
		particle.setPos(pos.x + dx,pos.y + dy,pos.z + dz);
	}
}
