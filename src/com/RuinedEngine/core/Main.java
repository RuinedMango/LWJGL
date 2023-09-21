package com.RuinedEngine.core;

import com.RuinedEngine.rendering.Render;

public class Main implements IAppLogic{
	public static void main(String[] args) {
		Main main = new Main();
		Engine gameEng = new Engine("Cool", new Window.WindowOptions(), main);
		gameEng.start();
	}
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Window window, Scene scene, Render render) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void input(Window window, Scene scene, long diffTimeMillis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Window window, Scene scene, long diffTimeMillis) {
		// TODO Auto-generated method stub
		
	}

}
