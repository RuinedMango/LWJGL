package com.RuinedEngine.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;

import com.RuinedEngine.audio.SoundBuffer;
import com.RuinedEngine.audio.SoundListener;
import com.RuinedEngine.audio.SoundManager;
import com.RuinedEngine.audio.SoundSource;
import com.RuinedEngine.core.Camera;
import com.RuinedEngine.core.ILogic;
import com.RuinedEngine.core.MouseInput;
import com.RuinedEngine.core.ObjectLoader;
import com.RuinedEngine.core.WindowManager;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Material;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.SceneManager;
import com.RuinedEngine.entity.Texture;
import com.RuinedEngine.entity.terrain.BlendMapTerrain;
import com.RuinedEngine.entity.terrain.Terrain;
import com.RuinedEngine.entity.terrain.TerrainTexture;
import com.RuinedEngine.lighting.DirectionalLight;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.rendering.Fog;
import com.RuinedEngine.rendering.RenderManager;
import com.RuinedEngine.utils.Consts;



public class TestGame implements ILogic{
	
	private List<Entity> entities;
	private final RenderManager renderer;
	private final ObjectLoader loader;
	private final WindowManager window;
	private Camera camera;
	private SceneManager sceneManager;
	Vector3f cameraInc;
	private SoundSource soundSource;
	private SoundManager soundManager;
	private Entity selectedEntity;
	private Fog fog;
	
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
		
		initSounds(camera.getPosition(), camera);
		Model model = loader.loadOBJModel("/models/Fiat.obj");
		model.setTexture(new Texture(loader.loadTexture("textures/punto_body.png")), 1f);
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/terrain.png"));
		TerrainTexture redTexture = new TerrainTexture(loader.loadTexture("textures/flowers.png"));
		TerrainTexture greenTexture = new TerrainTexture(loader.loadTexture("textures/stone.png"));
		TerrainTexture blueTexture = new TerrainTexture(loader.loadTexture("textures/dirt.png"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap.png"));
		entities = new ArrayList<>();
		Random rnd = new Random();
		for(int i = 0; i < 200; i++) {
			float x1 = rnd.nextFloat() * 100;
			float y1 = 5;
			float z = rnd.nextFloat() * -300;
			entities.add(new Entity(model,new Vector3f(x1,y1,z),new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180,0),1));
			sceneManager.addEntity(new Entity(model,new Vector3f(x1,y1,z),new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180,0),1));
		}
		entities.add(new Entity(model, new Vector3f(0,0,-2f), new Vector3f(0,0,0), 1));
		sceneManager.addEntity(new Entity(model, new Vector3f(0,0,-2f), new Vector3f(0,0,0), 1));
		
		BlendMapTerrain blendMapTerrain = new BlendMapTerrain(backgroundTexture, redTexture, greenTexture, blueTexture);
		
		Terrain terrain = new Terrain(new Vector3f(0,-1,-800), loader, new Material(new Vector4f(0f,0f,0f,0f), 0.1f), blendMapTerrain, blendMap);
		sceneManager.addTerrain(terrain);
		
		
		//point light
		float lightIntensity = 1f;
		Vector3f lightPosition = new Vector3f(0,0,0);
		Vector3f lightColour = new Vector3f(1,1,1);
		PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0,0,1);
		
		//spot light

		
		//directional light
		lightPosition = new Vector3f(-1,-10,0);
		lightColour = new Vector3f(1,1,1);
		sceneManager.setDirectionalLight(new DirectionalLight(lightColour,lightPosition,lightIntensity));
		
		sceneManager.setPointLights(new PointLight[]{pointLight});
		sceneManager.getPointLights()[0].setColour(new Vector3f(255,255,255));
		soundSource.play();
	}
	
	private void initSounds(Vector3f position,Camera camera) {
		soundManager = new SoundManager();
		soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
		soundManager.setListener(new SoundListener(camera.getPosition()));
		
		SoundBuffer buffer = new SoundBuffer("sound/cool.ogg");
		soundManager.addSoundBuffer(buffer);
		soundSource = new SoundSource(false, false);
		soundSource.setPosition(position);
		soundSource.setBuffer(buffer.getBufferId());
		soundManager.addSoundSource("CREAK", soundSource);
	}

	@Override
	public void input(WindowManager windowManager, SceneManager sceneManager) {
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
		Fog fog = new Fog(true, new Vector3f(0.5f, 0.5f, 255f),900f);
		soundManager.updateListenerPosition(camera);
		camera.movePosition(cameraInc.x * Consts.CAMERA_SPEED,cameraInc.y * Consts.CAMERA_SPEED,cameraInc.z * Consts.CAMERA_SPEED);
		camera.getRotation().x = Math.max(-85.0f, Math.min(camera.getRotation().x, 85.0f));
		if(mouseInput.isRightButtonPress()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
		}
		sceneManager.setLightAngle(sceneManager.getLightAngle() + 0.5f);
		if(sceneManager.getLightAngle() > 90) {
			sceneManager.getDirectionalLight().setIntensity(0);
			if(sceneManager.getLightAngle() >= 360) {
				sceneManager.setLightAngle(-90);
			}
		}else if(sceneManager.getLightAngle() <= -80 || sceneManager.getLightAngle() >= 80){
			float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
			sceneManager.getDirectionalLight().getColour().y = Math.max(factor, 0.9f);
			sceneManager.getDirectionalLight().getColour().z = Math.max(factor, 0.5f);
		}else {
			sceneManager.getDirectionalLight().setIntensity(1);
			sceneManager.getDirectionalLight().getColour().x = 1;
			sceneManager.getDirectionalLight().getColour().y = 1;
			sceneManager.getDirectionalLight().getColour().z = 1;
		}
		double angRad = Math.toRadians(sceneManager.getLightAngle());
		sceneManager.getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
		sceneManager.getDirectionalLight().getDirection().y = (float) Math.cos(angRad);
		
		for(Entity entity : sceneManager.getEntities()) {
			renderer.processEntity(entity);
		}
		for(Terrain terrain : sceneManager.getTerrains()) {
			renderer.processTerrain(terrain);
		}
		sceneManager.getPointLights()[0].getPosition().x = camera.getPosition().x;
		sceneManager.getPointLights()[0].getPosition().y = camera.getPosition().y;
		sceneManager.getPointLights()[0].getPosition().z = camera.getPosition().z;


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
		soundManager.cleanup();
		renderer.cleanup();
		loader.cleanup();
	}
	public Entity getSelectedEntity() {
		return selectedEntity;
	}
	public void setSelectedEntity(Entity selectedEntity) {
		this.selectedEntity = selectedEntity;
	}
}
