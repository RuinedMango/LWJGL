package main.java.com.ruinedmango.gemed.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import main.java.com.ruinedmango.gemed.lighting.DirectionalLight;
import main.java.com.ruinedmango.gemed.lighting.PointLight;
import main.java.com.ruinedmango.gemed.lighting.SpotLight;



public class TestGame implements ILogic{
	
	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	
	private List<Entity> entities;
	private Camera camera;
	
	Vector3f cameraInc;
	
	private float lightAngle;
	private DirectionalLight directionalLight;
	private PointLight[] pointLights;
	private SpotLight[] spotLights;
	
	public TestGame() {
		renderer = new RenderManager();
		window = Launcher.getWindow();
		loader = new ObjectLoader();
		camera = new Camera();
		cameraInc = new Vector3f(0,0,0);
		lightAngle = -90;
	}
	
	@Override
	public void init() throws Exception {
		renderer.init();
		
		Model model = loader.loadOBJModel("/models/Fiat.obj");
		model.setTexture(new Texture(loader.loadTexture("textures/punto_body.png")), 1f);
		
		entities = new ArrayList<>();
		Random rnd = new Random();
		for(int i = 0; i < 200; i++) {
			float x = rnd.nextFloat() * 100 - 50;
			float y = rnd.nextFloat() * 100 - 50;
			float z = rnd.nextFloat() * -300;
			entities.add(new Entity(model,new Vector3f(x,y,z),new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180,0),1));
		}
		entities.add(new Entity(model, new Vector3f(0,0,-2f), new Vector3f(0,0,0), 1));
		//point light
		float lightIntensity = 5.0f;
		Vector3f lightPosition = new Vector3f(0,0,0);
		Vector3f lightColour = new Vector3f(1,1,1);
		PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0,0,1);
		
		//spot light
		Vector3f coneDir = new Vector3f(0,0,1);
		float cutoff = (float) Math.cos(Math.toRadians(180));
		SpotLight spotLight = new SpotLight(new PointLight(lightColour, new Vector3f(0,0,-3.6f), lightIntensity,0,0,0.2f), coneDir, cutoff);
		
		SpotLight spotLight1 = new SpotLight(pointLight,coneDir,cutoff);
		
		//directional light
		lightPosition = new Vector3f(-1,-10,0);
		lightColour = new Vector3f(1,1,1);
		directionalLight = new DirectionalLight(lightColour,lightPosition,lightIntensity);
		
		pointLights = new PointLight[]{pointLight};
		spotLights = new SpotLight[]{spotLight, spotLight1};
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
		
		if(mouseInput.isRightButtonPress()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
		}
		//entity.incRotation(0, 0.25f, 0);
		lightAngle += 0.5f;
		if(lightAngle > 90) {
			directionalLight.setIntensity(0);
			if(lightAngle >= 360) {
				lightAngle = -90;
			}
		}else if(lightAngle <= -80 || lightAngle >= 80){
			float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
			directionalLight.getColour().y = Math.max(factor, 0.9f);
			directionalLight.getColour().z = Math.max(factor, 0.5f);
		}else {
			directionalLight.setIntensity(1);
			directionalLight.getColour().x = 1;
			directionalLight.getColour().y = 1;
			directionalLight.getColour().z = 1;
		}
		double angRad = Math.toRadians(lightAngle);
		directionalLight.getDirection().x = (float) Math.sin(angRad);
		directionalLight.getDirection().y = (float) Math.cos(angRad);
		
		for(Entity entity : entities) {
			renderer.processEntity(entity);
		}
		pointLights[0].getPosition().x = camera.getPosition().x;
		pointLights[0].getPosition().y = camera.getPosition().y;
		pointLights[0].getPosition().z = camera.getPosition().z;
	}

	@Override
	public void render() {
		if(window.isResize()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(true);
		}
		renderer.render(camera, directionalLight, pointLights, spotLights);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		loader.cleanup();
	}

}
