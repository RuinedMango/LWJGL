package com.RuinedEngine.test;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.RuinedEngine.core.Camera;
import com.RuinedEngine.core.ILogic;
import com.RuinedEngine.core.MouseInput;
import com.RuinedEngine.core.ObjectLoader;
import com.RuinedEngine.core.WindowManager;
import com.RuinedEngine.entity.SceneManager;
import com.RuinedEngine.lighting.DirectionalLight;
import com.RuinedEngine.rendering.RenderManager;
import com.RuinedEngine.utils.Consts;



public class TestGame implements ILogic{
	
	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	private Camera camera;
	private SceneManager sceneManager;
	Vector3f cameraInc;
	
	public TestGame() {
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0,0,0);
		sceneManager = new SceneManager(-90);
	}
	
	@Override
	public void init() throws Exception {
		renderer.init();
		
		
		//point light

		
		//spot light

		
		//directional light
		float lightIntensity = 1;
		Vector3f lightPosition = new Vector3f(-1,-10,0);
		Vector3f lightColour = new Vector3f(1,1,1);
		sceneManager.setDirectionalLight(new DirectionalLight(lightColour,lightPosition,lightIntensity));
		
	}

	@Override
	public void input() {
		cameraInc.set(0,0,0);
		if(window.isKeyPressed(GLFW.GLFW_KEY_W)) {
			cameraInc.z = -1;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_S)) {
			cameraInc.z = 1;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_A)) {
			cameraInc.x = -1;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_D)) {
			cameraInc.x = 1;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
			cameraInc.y = -1;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_X)) {
			cameraInc.y = 1;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			Consts.CAMERA_SPEED = 0.2f;
		}
		if(window.isKeyReleased(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			Consts.CAMERA_SPEED = 0.05f;
		}
	}

	@Override
	public void update(MouseInput mouseInput) {
		camera.movePosition(cameraInc.x * Consts.CAMERA_SPEED,cameraInc.y * Consts.CAMERA_SPEED,cameraInc.z * Consts.CAMERA_SPEED);
		camera.getRotation().x = Math.max(-85.0f, Math.min(camera.getRotation().x, 85.0f));
		if(mouseInput.isRightButtonPress()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
		}
	}

	@Override
	public void render() {
		if(window.isResize()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(true);
		}
		renderer.render(camera, sceneManager);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		loader.cleanup();
	}

}
