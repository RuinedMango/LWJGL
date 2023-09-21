package com.RuinedEngine.core;

import java.util.concurrent.Callable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

public class Window {
	private final long windowHandle;
	
	private int height;
	private Callable<Void> resizeFunc;
	private int width;
	
	public Window(String title, WindowOptions opts, Callable<Void> resizeFunc) {
		this.resizeFunc = resizeFunc;
		if(!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
		
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		if(opts.compatibleProfile) {
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
		}else {
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		}
		if(opts.width > 0 && opts.height > 0) {
			this.width = opts.width;
			this.height = opts.height;
		}else {
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
			GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			width = vidMode.width();
			height = vidMode.height();
		}
		windowHandle = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
		if(windowHandle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resized(w, h));
		
		GLFW.glfwSetKeyCallback(windowHandle, (window, key ,scancode, action, mods)->{
			keyCallBack(key, action);
		});
		
		GLFW.glfwMakeContextCurrent(windowHandle);
		if(opts.fps > 0) {
			GLFW.glfwSwapInterval(0);
		}else {
			GLFW.glfwSwapInterval(1);
		}
		GLFW.glfwShowWindow(windowHandle);
		
		int[] arrWidth = new int[1];
		int[] arrHeight = new int[1];
		GLFW.glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
		width = arrWidth[0];
		height = arrHeight[0];
	}
	
	public void keyCallBack(int key, int action) {
		if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
			GLFW.glfwSetWindowShouldClose(windowHandle, true);
		}
	}
	
	public void cleanup() {
		glfwFreeCallbacks(windowHandle);
		GLFW.glfwDestroyWindow(windowHandle);
		GLFW.glfwTerminate();
		GLFWErrorCallback callback = GLFW.glfwSetErrorCallback(null);
		if(callback != null) {
			callback.free();
		}
	}
	
	public long getWindowHandle() {
		return windowHandle;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public boolean isKeyPressed(int keyCode) {
		return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
	}
	
	public void pollEvents() {
		GLFW.glfwPollEvents();
	}
	protected void resized(int width, int height) {
		this.width = width;
		this.height = height;
		try {
			resizeFunc.call();
		}catch(Exception excp) {
			
		}
	}
	public void update() {
		GLFW.glfwSwapBuffers(windowHandle);
	}
	public boolean windowShouldClose() {
		return GLFW.glfwWindowShouldClose(windowHandle);
	}

	public static class WindowOptions{
		public boolean compatibleProfile;
		public int fps;
		public int height;
		public int ups = Engine.TARGET_UPS;
		public int width;
	}
}
