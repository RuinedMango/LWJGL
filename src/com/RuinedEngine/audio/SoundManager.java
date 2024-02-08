package com.RuinedEngine.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.SOFTHRTF;

import com.RuinedEngine.core.Camera;

import static org.lwjgl.system.MemoryUtil.NULL;
public class SoundManager {
	private final List<SoundBuffer> soundBufferList;
	private final Map<String, SoundSource> soundSourceMap;
	private long context;
	private long device;
	private SoundListener listener;
	
	public SoundManager() {
		soundBufferList = new ArrayList<>();
		soundSourceMap = new HashMap<>();
		
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if(device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if(context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}
	public void addSoundBuffer(SoundBuffer soundBuffer) {
		this.soundBufferList.add(soundBuffer);
	}
	public void addSoundSource(String name, SoundSource soundSource) {
		this.soundSourceMap.put(name, soundSource);
	}
	public void cleanup() {
		soundSourceMap.values().forEach(SoundSource::cleanup);
		soundSourceMap.clear();
		soundBufferList.forEach(SoundBuffer::cleanup);
		soundBufferList.clear();
		if(context != NULL) {
			ALC10.alcDestroyContext(context);
		}
		if(device != NULL) {
			ALC10.alcCloseDevice(device);
		}
	}
	public SoundListener getListener(){
		return this.listener;
	}
	public SoundSource getSoundSource(String name) {
		return this.soundSourceMap.get(name);
	}
	public void playSoundSource(String name) {
		SoundSource soundSource = this.soundSourceMap.get(name);
		if(soundSource != null && !soundSource.isPlaying()) {
			soundSource.play();
		}
	}
	public void removeSoundSource(String name) {
		this.soundSourceMap.remove(name);
	}
	public void setAttenuationModel(int model) {
		AL10.alDistanceModel(model);
	}
	public void setListener(SoundListener listener) {
		this.listener = listener;
	}
	public void setHRTF(boolean enable) {
		int num_hrtf = ALC10.alcGetInteger(device, 6548);
		if(num_hrtf == 0) {
			System.out.println("No HRTFs found");
		}else {
			if(!SOFTHRTF.alcResetDeviceSOFT(device, new int[]{6546, enable ? 1 : 0, 0})) {
				System.out.println("Failed to reset HRTF");
			}
			int hrtf_state = ALC10.alcGetInteger(device, 6546);
			if(hrtf_state == 0 && enable) {
				System.out.println("HRTF not Enabled");
			}
		}
	}
	public void updateListenerPosition(Camera camera) {
		Matrix4f viewMatrix = camera.getViewMatrix();
		listener.setPosition(camera.getPosition());
		Vector3f at = new Vector3f();
		viewMatrix.positiveZ(at).negate();
		Vector3f up = new Vector3f();
		viewMatrix.positiveY(up);
		listener.setOrientation(at, up);
	}
	
}
