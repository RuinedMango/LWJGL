package main.java.com.ruinedmango.gemed.test;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import main.java.com.ruinedmango.gemed.core.Camera;
import main.java.com.ruinedmango.gemed.core.ILogic;
import main.java.com.ruinedmango.gemed.core.MouseInput;
import main.java.com.ruinedmango.gemed.core.ObjectLoader;
import main.java.com.ruinedmango.gemed.core.RenderManager;
import main.java.com.ruinedmango.gemed.core.WindowManager;
import main.java.com.ruinedmango.gemed.core.entity.Entity;
import main.java.com.ruinedmango.gemed.core.entity.Model;
import main.java.com.ruinedmango.gemed.core.entity.Texture;
import main.java.com.ruinedmango.gemed.core.utils.Consts;



public class TestGame implements ILogic{
	
	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	
	private Entity entity;
	private Camera camera;
	
	Vector3f cameraInc;
	
	public TestGame() {
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0,0,0);
	}
	
	@Override
	public void init() throws Exception {
		renderer.init();

		Model model = loader.loadOBJModel("/models/Fiat.obj");
		model.setTexture(new Texture(loader.loadTexture("textures/punto_body.png")));
		entity = new Entity(model,new Vector3f(0,0,-5),new Vector3f(0,0,0),1);
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
	}

	@Override
	public void update(MouseInput mouseInput) {
		camera.movePosition(cameraInc.x * Consts.CAMERA_SPEED,cameraInc.y * Consts.CAMERA_SPEED,cameraInc.z * Consts.CAMERA_SPEED);
		
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
		window.setClearColour(0.0f,0.0f,0.0f,0.0f);
		renderer.render(entity, camera);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		loader.cleanup();
	}

}
