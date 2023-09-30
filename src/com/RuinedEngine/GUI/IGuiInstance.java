package com.RuinedEngine.GUI;

import com.RuinedEngine.core.Scene;
import com.RuinedEngine.core.Window;

public interface IGuiInstance {
	void drawGui();
	
	boolean handleGuiInput(Scene scene, Window window);
}
