package com.RuinedEngine.core;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.RuinedEngine.GUI.IGuiInstance;
import com.RuinedEngine.GUI.LightControls;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.ModelLoader;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.lighting.SceneLights;
import com.RuinedEngine.lighting.SpotLight;
import com.RuinedEngine.utils.Consts;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;

public class Main implements IAppLogic, IGuiInstance{
	public static void main(String[] args) {
		Main main = new Main();
		Engine gameEng = new Engine("Noice", new Window.WindowOptions(), main);
		gameEng.start();
	}
	
	@Override
	public void cleanup() {
   // TODO document why this method is empty
	}

	@Override
	public void init(Window window, Scene scene, Render render) {
		Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/Knife/knife.obj", scene.getTextureCache());
		scene.addModel(cubeModel);
		
		Entity cubeEntity = new Entity("cube-entity", cubeModel.getId());
		cubeEntity.setPosition(0, 0, -2);
		cubeEntity.updateModelMatrix();
		scene.addEntity(cubeEntity);
		
		SceneLights sceneLights = new SceneLights();
		sceneLights.getAmbientLight().setIntensity(0.3f);
		scene.setSceneLights(sceneLights);
		sceneLights.getPointLights().add(new PointLight(new Vector3f(1,1,1), new Vector3f(0,0,-1.4f), 1.0f));
		Vector3f coneDir = new Vector3f(0,0,-1);
		sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));
		
		LightControls lightControls = new LightControls(scene);
		scene.setGuiInstance(lightControls);
	}

	@Override
	public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
		if(inputConsumed) {
			return;
		}
		float move = diffTimeMillis * Consts.MOVEMENT_SPEED;
		Camera camera = scene.getCamera();
		if(window.isKeyPressed(GLFW.GLFW_KEY_W)) {
			camera.moveForward(move);
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_S)) {
			camera.moveBackwards(move);
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_A)) {
			camera.moveRight(move);
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_D)) {
			camera.moveLeft(move);
		}
		if(window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
			camera.moveUp(move);
		}else if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			camera.moveDown(move);
		}
		MouseInput mouseInput = window.getMouseInput();
		if(mouseInput.isRightButtonPressed()) {
			Vector2f displVec = mouseInput.getDisplVec();
			camera.addRotation((float) Math.toRadians(-displVec.x * Consts.MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * Consts.MOUSE_SENSITIVITY));
		}
	}

	@Override
	public void update(Window window, Scene scene, long diffTimeMillis) {
   // TODO document why this method is empty
	}

	@Override
	public void drawGui() {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0,0,ImGuiCond.Always);
		ImGui.showUserGuide();
		ImGui.endFrame();
		ImGui.render();
	}

	@Override
	public boolean handleGuiInput(Scene scene, Window window) {
		ImGuiIO imGuiIO = ImGui.getIO();
		MouseInput mouseInput = window.getMouseInput();
		Vector2f mousePos = mouseInput.getCurrentPos();
		imGuiIO.setMousePos(mousePos.x, mousePos.y);
		imGuiIO.setMouseDown(0, mouseInput.isLeftButtonPressed());
		imGuiIO.setMouseDown(1, mouseInput.isRightButtonPressed());
		return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
	}

}
