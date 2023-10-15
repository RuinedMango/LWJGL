package com.RuinedEngine.core;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.RuinedEngine.VFX.Fog;
import com.RuinedEngine.VFX.SkyBox;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.ModelLoader;
import com.RuinedEngine.lighting.AmbientLight;
import com.RuinedEngine.lighting.DirLight;
import com.RuinedEngine.lighting.SceneLights;
import com.RuinedEngine.utils.AnimationData;
import com.RuinedEngine.utils.Consts;

public class Main implements IAppLogic{
	private float lightAngle;
	private AnimationData animationData;
	public static void main(String[] args) {
		Main main = new Main();
		Window.WindowOptions opts = new Window.WindowOptions();
		opts.antiAliasing = true;
		Engine gameEng = new Engine("Noice", new Window.WindowOptions(), main);
		gameEng.start();
	}
	
	@Override
	public void cleanup() {
   // TODO document why this method is empty
	}

	@Override
	public void init(Window window, Scene scene, Render render) {
		String terrainModelId = "terrain";
        Model terrainModel = ModelLoader.loadModel(terrainModelId, "resources/models/terrain/quad.obj",
                scene.getTextureCache(), false);
        scene.addModel(terrainModel);
        Entity terrainEntity = new Entity("terrainEntity", terrainModelId);
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);

        String bobModelId = "bobModel";
        Model bobModel = ModelLoader.loadModel(bobModelId, "resources/models/bob/boblamp.md5mesh",
                scene.getTextureCache(), true);
        scene.addModel(bobModel);
        Entity bobEntity = new Entity("bobEntity", bobModelId);
        bobEntity.setScale(0.05f);
        bobEntity.updateModelMatrix();
        animationData = new AnimationData(bobModel.getAnimationList().get(0));
        bobEntity.setAnimationData(animationData);
        scene.addEntity(bobEntity);

        SceneLights sceneLights = new SceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);

        DirLight dirLight = sceneLights.getDirLight();
        dirLight.setPosition(0, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLights(sceneLights);

        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(100);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkyBox(skyBox);

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        lightAngle = 45.001f;
	}

	@Override
	public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * Consts.MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            lightAngle -= 2.5f;
            if (lightAngle < -90) {
                lightAngle = -90;
            }
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            lightAngle += 2.5f;
            if (lightAngle > 90) {
                lightAngle = 90;
            }
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * Consts.MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * Consts.MOUSE_SENSITIVITY));
        }

        SceneLights sceneLights = scene.getSceneLights();
        DirLight dirLight = sceneLights.getDirLight();
        double angRad = Math.toRadians(lightAngle);
        dirLight.getDirection().x = (float) Math.sin(angRad);
        dirLight.getDirection().y = (float) Math.cos(angRad);
	}

	@Override
	public void update(Window window, Scene scene, long diffTimeMillis) {
		animationData.nextFrame();
	}
}
