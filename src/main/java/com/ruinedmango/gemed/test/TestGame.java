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
import main.java.com.ruinedmango.gemed.lighting.DirectionalLight;
import main.java.com.ruinedmango.gemed.lighting.PointLight;
import main.java.com.ruinedmango.gemed.lighting.SpotLight;



public class TestGame implements ILogic{
	
	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	
	private Entity entity;
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
		entity = new Entity(model,new Vector3f(0,0,-5),new Vector3f(0,0,0),1);
		//point light
		float lightIntensity = 20.0f;
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
		if(window.isKeyPressed(GLFW.GLFW_KEY_O)) {
			pointLights[0].getPosition().x += 0.1f;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_P)) {
			pointLights[0].getPosition().x -= 0.1f;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_L)) {
			pointLights[0].getPosition().y += 0.1f;
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_K)) {
			pointLights[0].getPosition().y -= 0.1f;
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
	}

	@Override
	public void render() {
		if(window.isResize()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResize(true);
		}
		renderer.render(entity, camera, directionalLight, pointLights, spotLights);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		loader.cleanup();
	}

}
