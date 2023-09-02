package main.java.com.ruinedmango.gemed.test;

import main.java.com.ruinedmango.gemed.core.EngineManager;
import main.java.com.ruinedmango.gemed.core.WindowManager;
import main.java.com.ruinedmango.gemed.core.utils.Consts;

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
