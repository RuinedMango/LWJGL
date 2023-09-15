package com.RuinedEngine.VFX;

import java.util.List;

import com.RuinedEngine.entity.Entity;

public interface IParticleEmitter {
	void cleanup();
	
	Particle getBaseParticle();
	
	List<Entity> getParticles();
}
