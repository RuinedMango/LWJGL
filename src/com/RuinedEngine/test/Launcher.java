package com.RuinedEngine.test;

import com.RuinedEngine.core.EngineManager;
import com.RuinedEngine.core.WindowManager;
import com.RuinedEngine.utils.Consts;

public class Launcher {
	private static WindowManager window;
	private static TestGame game;

	public static void main(String[] args) {
		window = new WindowManager(Consts.TITLE, 0,0,true);
		game = new TestGame();
		EngineManager engine = new EngineManager();
		try {
			engine.start();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static TestGame getGame() {
		return game;
	}
	public static WindowManager getWindow() {
		return window;
	}
}
