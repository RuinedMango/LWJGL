package com.RuinedEngine.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class SoundSource {
	public final int sourceId;
	
	public SoundSource(boolean loop, boolean relative) {
		this.sourceId = AL10.alGenSources();
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
		AL10.alSourcei(sourceId, AL10.AL_SOURCE_RELATIVE, relative ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	public void cleanup() {
		stop();
		AL10.alDeleteSources(sourceId);
	}
	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	public void pause() {
		AL10.alSourcePause(sourceId);
	}
	public void play() {
		AL10.alSourcePlay(sourceId);
	}
	public void setBuffer(int bufferId) {
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
	}
	public void setGain(float gain) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
	}
	public void setPosition(Vector3f position) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, position.x, position.y, position.z);
	}
	public void stop() {
		AL10.alSourceStop(sourceId);
	}
}
