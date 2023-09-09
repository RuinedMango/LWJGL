package com.RuinedEngine.core;

import com.RuinedEngine.entity.SceneManager;

public interface ILogic {
	void init() throws Exception;
	
	void input(WindowManager windowManager, SceneManager sceneManager);
	
	void update(MouseInput mouseInput);
	
	void render();
	
	void cleanup();
}
