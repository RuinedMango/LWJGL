package com.RuinedEngine.utils;

import java.util.Arrays;

import org.joml.Matrix4f;

import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.ModelLoader;

public class AnimationData {
	public static final Matrix4f[] DEFAULT_BONES_MATRICES = new Matrix4f[ModelLoader.MAX_BONES];
	
	static {
		Matrix4f zeroMatrix = new Matrix4f().zero();
		Arrays.fill(DEFAULT_BONES_MATRICES, zeroMatrix);
	}
	
	private Model.Animation currentAnimation;
	private int currentFrameIdx;
	
	public AnimationData(Model.Animation currentAnimation) {
		currentFrameIdx = 0;
		this.currentAnimation = currentAnimation;
	}
	public Model.Animation getCurrentAnimation(){
		return currentAnimation;
	}
	public Model.AnimatedFrame getCurrentFrame(){
		return currentAnimation.frames().get(currentFrameIdx);
	}
	public int getCurrentFrameIdx() {
		return currentFrameIdx;
	}
	public void nextFrame() {
		int nextFrame = currentFrameIdx + 1;
		if(nextFrame > currentAnimation.frames().size() - 1) {
			currentFrameIdx = 0;
		}else {
			currentFrameIdx = nextFrame;
		}
	}
	public void setCurrentAnimation(Model.Animation currentAnimation) {
		currentFrameIdx = 0;
		this.currentAnimation = currentAnimation;
	}
}
