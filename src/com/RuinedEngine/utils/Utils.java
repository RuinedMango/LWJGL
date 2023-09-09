package com.RuinedEngine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import com.RuinedEngine.core.Camera;

public class Utils {
	public static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
		buffer.put(data).flip();
		return buffer;
	}
	public static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
		buffer.put(data).flip();
		return buffer;
	}
	
	public static String loadResource(String filename)throws Exception{
		String result;
		try(InputStream in = Utils.class.getResourceAsStream(filename);Scanner scanner = new Scanner(in,StandardCharsets.UTF_8.name())){
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}
	public static List<String> readAllLines(String fileName){
		List<String> list = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))){
			String line;
			while((line = br.readLine()) != null) {
				list.add(line);
			}
		}catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().x), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().y),   new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().z),  new Vector3f(0, 0, 1));
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos);

        // Doesn't seem to work right without this transpose here for some reason.
        viewMatrix.transpose();

        return viewMatrix;
    }
}
