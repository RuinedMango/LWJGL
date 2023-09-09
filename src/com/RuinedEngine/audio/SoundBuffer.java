package com.RuinedEngine.audio;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL10;

import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBVorbis;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundBuffer {
	private final int bufferId;
	
	private ShortBuffer pcm;
	
	public SoundBuffer(String filePath) {
		this.bufferId = AL10.alGenBuffers();
		try(STBVorbisInfo info = STBVorbisInfo.malloc()){
			pcm = readVorbis(filePath, info);
			AL10.alBufferData(bufferId, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm, info.sample_rate());
		}
	}
	public void cleanup() {
		AL10.alDeleteBuffers(this.bufferId);
		if(pcm != null) {
			MemoryUtil.memFree(pcm);
		}
	}
	public int getBufferId() {
		return this.bufferId;
	}
	private ShortBuffer readVorbis(String filePath, STBVorbisInfo info) {
		try(MemoryStack stack = MemoryStack.stackPush()){
			IntBuffer error = stack.mallocInt(1);
			long decoder = STBVorbis.stb_vorbis_open_filename(filePath, error,null);
			if(decoder == NULL) {
				throw new RuntimeException("Failed to open OGG Vorbis file");
			}
			STBVorbis.stb_vorbis_get_info(decoder, info);
			int channels = info.channels();
			
			int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
			
			ShortBuffer result = MemoryUtil.memAllocShort(lengthSamples * channels);
			result.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, result) * channels);
			STBVorbis.stb_vorbis_close(decoder);
			
			return result;
		}
	}
}
