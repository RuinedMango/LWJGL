package com.RuinedEngine.VFX;

import org.joml.Vector3f;

import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Model;

public class Particle extends Entity {
	
	private Vector3f speed;
	/**Determines how long a particle lives*/
	private long ttl;
	
	
	public Particle(Model model, Vector3f speed, long ttl) {
		super(model);
		this.speed = new Vector3f(speed);
		this.ttl = ttl;
	}

	public Particle(Particle baseParticle) {
		super(baseParticle.getModel());
		Vector3f aux = baseParticle.getPos();
		baseParticle.setPos(aux.x,aux.y,aux.z);
		aux = baseParticle.getRotation();
		baseParticle.setRotation(aux.x, aux.y, aux.z);
		baseParticle.setScale(baseParticle.getScale());
		this.speed = new Vector3f(baseParticle.speed);
		this.ttl = baseParticle.getTtl();
	}

	public Vector3f getSpeed() {
		return speed;
	}

	public void setSpeed(Vector3f speed) {
		this.speed = speed;
	}

	public long getTtl() {
		return ttl;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}
	/**
	 * Updates the Particle's TTL.
	 * @param elaspedTime Elapsed Time in Milliseconds.
	 * @return The Particles TTL.
	 */
	public long updateTtl(long elapsedTime) {
		this.ttl -= elapsedTime;
		return this.ttl;
	}
}
