package com.RuinedEngine.core;

import com.RuinedEngine.GUI.LightControls;
import com.RuinedEngine.VFX.Fog;
import com.RuinedEngine.VFX.SkyBox;
import com.RuinedEngine.audio.SoundBuffer;
import com.RuinedEngine.audio.SoundListener;
import com.RuinedEngine.audio.SoundManager;
import com.RuinedEngine.audio.SoundSource;
import com.RuinedEngine.entity.Entity;
import com.RuinedEngine.entity.Model;
import com.RuinedEngine.entity.ModelLoader;
import com.RuinedEngine.lighting.AmbientLight;
import com.RuinedEngine.lighting.DirLight;
import com.RuinedEngine.lighting.PointLight;
import com.RuinedEngine.lighting.SceneLights;
import com.RuinedEngine.lighting.SpotLight;
import com.RuinedEngine.utils.AnimationData;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.openal.AL11;

public class Main implements IAppLogic {

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private AnimationData animationData;
    private float lightAngle;
    private LightControls lightControls;
    private SoundManager soundMgr;
    private SoundSource playerSoundSource;

    public static void main(String[] args) {
        Main main = new Main();
        Window.WindowOptions opts = new Window.WindowOptions();
        opts.antiAliasing = true;
        Engine gameEng = new Engine("RuinedEngine", opts, main);
        gameEng.getWindow().setIcon("resources/icon.png");
        gameEng.getWindow().setCursorIcon("resources/icon.png", 0, 0);
        gameEng.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    	soundMgr.cleanup();
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
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));

        SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
        skyBox.getSkyBoxEntity().setScale(100);
        skyBox.getSkyBoxEntity().updateModelMatrix();
        scene.setSkyBox(skyBox);

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        lightAngle = 45.001f;
        initSounds(bobEntity.getPosition(), camera);
        lightControls = new LightControls(scene, soundMgr);
        scene.setGuiInstance(lightControls);
        
    }
    
    private void initSounds(Vector3f position, Camera camera) {
    	soundMgr = new SoundManager();
    	soundMgr.setListener(new SoundListener(camera.getPosition()));
    	
    	SoundBuffer buffer = new SoundBuffer("resources/sounds/creak1.ogg");
    	soundMgr.addSoundBuffer(buffer);
    	playerSoundSource = new SoundSource(false, false);
    	playerSoundSource.setPosition(position);
    	playerSoundSource.setBuffer(buffer.getBufferId());
    	soundMgr.addSoundSource("CREAK", playerSoundSource);
    	
    	buffer = new SoundBuffer("resources/sounds/woo_scary.ogg");
    	soundMgr.addSoundBuffer(buffer);
    	SoundSource source = new SoundSource(true, true);
    	source.setBuffer(buffer.getBufferId());
    	soundMgr.addSoundSource("MUSIC", source);
    	source.play();
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
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
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
        soundMgr.updateListenerPosition(camera);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        animationData.nextFrame();
        if(animationData.getCurrentFrameIdx() == 45) {
        	playerSoundSource.play();
        }
    }
}
