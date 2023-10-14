package com.RuinedEngine.lighting;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.RuinedEngine.core.Scene;

public class CascadeShadow {
	public static final int SHADOW_MAP_CASCADE_COUNT = 3;
	
	private Matrix4f projViewMatrix;
	private float splitDistance;
	
	public CascadeShadow() {
		projViewMatrix = new Matrix4f();
	}
	public static void updateCascadeShadows(List<CascadeShadow> cascadeShadows, Scene scene) {
		Matrix4f viewMatrix = scene.getCamera().getViewMatrix();
		Matrix4f projMatrix = scene.getProjection().getProjMatrix();
		Vector4f lightPos = new Vector4f(scene.getSceneLights().getDirLight().getDirection(), 0);
		
		float cascadeSplitLambda = 0.95f;
		
		float[] cascadeSplits = new float[SHADOW_MAP_CASCADE_COUNT];
		
		float nearClip = projMatrix.perspectiveNear();
		float farClip = projMatrix.perspectiveFar();
		float clipRange = farClip - nearClip;
		
		float minZ = nearClip;
		float maxZ = nearClip + clipRange;
		
		float range = maxZ - minZ;
		float ratio = maxZ / minZ;
		
		for(int i = 0; i < SHADOW_MAP_CASCADE_COUNT; i++) {
			float p = (i + 1) / (float) (SHADOW_MAP_CASCADE_COUNT);
			float log = (float) (minZ * java.lang.Math.pow(ratio, p));
			float uniform = minZ + range * p;
			float d = cascadeSplitLambda * (log - uniform) + uniform;
			cascadeSplits[i] = (d - nearClip) / clipRange;
		}
		float lastSplitDist = 0.0f;
		for(int i = 0; i < SHADOW_MAP_CASCADE_COUNT; i++) {
			float splitDist = cascadeSplits[i];
			
			Vector3f[] frustrumCorners = new Vector3f[] {
					new Vector3f(-1.0f, 1.0f, -1.0f),
					new Vector3f(1.0f, 1.0f, -1.0f),
					new Vector3f(1.0f, -1.0f, -1.0f),
					new Vector3f(-1.0f, -1.0f, -1.0f),
					new Vector3f(-1.0f, 1.0f, 1.0f),
					new Vector3f(1.0f, 1.0f, 1.0f),
					new Vector3f(1.0f, -1.0f, 1.0f),
					new Vector3f(-1.0f, -1.0f, 1.0f),
			};
			Matrix4f invCam = (new Matrix4f(projMatrix).mul(viewMatrix)).invert();
			for(int j = 0; j < 8; j++) {
				Vector4f invCorner = new Vector4f(frustrumCorners[j], 1.0f).mul(invCam);
				frustrumCorners[j] = new Vector3f(invCorner.x / invCorner.w, invCorner.y / invCorner.w, invCorner.z / invCorner.w);
			}
			for(int j = 0; j < 4; i++) {
				Vector3f dist = new Vector3f(frustrumCorners[j + 4]).sub(frustrumCorners[j]);
				frustrumCorners[j + 4] = new Vector3f(frustrumCorners[j]).add(new Vector3f(dist).mul(splitDist));
				frustrumCorners[j] = new Vector3f(frustrumCorners[j]).add(new Vector3f(dist).mul(lastSplitDist));
			}
			
			Vector3f frustrumCenter = new Vector3f(0.0f);
			for(int j = 0; j < 8; j++) {
				frustrumCenter.add(frustrumCorners[j]);
			}
			frustrumCenter.div(8.0f);
			
			float radius = 0.0f;
			for(int j = 0; j < 8; j++) {
				float distance = (new Vector3f(frustrumCorners[j]).sub(frustrumCenter)).length();
				radius = java.lang.Math.max(radius, distance);
			}
			radius = (float) java.lang.Math.ceil(radius * 16.0f) / 16.0f;
			
			Vector3f maxExtents = new Vector3f(radius);
			Vector3f minExtents = new Vector3f(maxExtents).mul(-1);
			
			Vector3f lightDir = (new Vector3f(lightPos.x, lightPos.y, lightPos.z).mul(-1)).normalize();
			Vector3f eye = new Vector3f(frustrumCenter).sub(new Vector3f(lightDir).mul(-minExtents.z));
			Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
			Matrix4f lightViewMatrix = new Matrix4f().lookAt(eye, frustrumCenter, up);
			Matrix4f lightOrthoMatrix = new Matrix4f().ortho(minExtents.x, maxExtents.x, minExtents.y, maxExtents.y, minExtents.z, maxExtents.z);
			
			CascadeShadow cascadeShadow = cascadeShadows.get(i);
			cascadeShadow.splitDistance = (nearClip + splitDist * clipRange) * -1.0f;
			cascadeShadow.projViewMatrix = lightOrthoMatrix.mul(lightViewMatrix);
			
			lastSplitDist = cascadeSplits[i];
		}
		
	}
	public Matrix4f getProjViewMatrix() {
		return projViewMatrix;
	}
	public float getSplitDistance() {
		return splitDistance;
	}
}
