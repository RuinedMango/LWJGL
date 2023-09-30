package com.RuinedEngine.rendering;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.RuinedEngine.GUI.GuiMesh;
import com.RuinedEngine.GUI.IGuiInstance;
import com.RuinedEngine.core.Scene;
import com.RuinedEngine.core.Window;
import com.RuinedEngine.entity.Texture;
import com.RuinedEngine.utils.UniformsMap;

import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;

public class GuiRender {
	
	private GuiMesh guiMesh;
	private GLFWKeyCallback prevKeyCallback;
	private Vector2f scale;
	private ShaderProgram shaderProgram;
	private Texture texture;
	private UniformsMap uniformsMap;
	
	public GuiRender(Window window) {
		List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/gui.vert", GL20.GL_VERTEX_SHADER));
		shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("resources/shaders/gui.frag", GL20.GL_FRAGMENT_SHADER));
		shaderProgram = new ShaderProgram(shaderModuleDataList);
		createUniforms();
		createUIResources(window);
		setupKeyCallback(window);
	}
	public void cleanup() {
		shaderProgram.cleanup();
		texture.cleanup();
		if(prevKeyCallback != null) {
			prevKeyCallback.free();
		}
	}
	private void createUIResources(Window window){
		ImGui.createContext();
		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setIniFilename(null);
		imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());
		
		ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
		ImInt width = new ImInt();
		ImInt height = new ImInt();
		ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
		texture = new Texture(width.get(),height.get(),buf);
		
		guiMesh = new GuiMesh();
	}
	private void createUniforms() {
		uniformsMap = new UniformsMap(shaderProgram.getProgramId());
		uniformsMap.createUniform("scale");
		scale = new Vector2f();
	}
	private void setupKeyCallback(Window window) {
		ImGuiIO io = ImGui.getIO();
		io.setKeyMap(ImGuiKey.Tab, GLFW.GLFW_KEY_TAB);
		io.setKeyMap(ImGuiKey.LeftArrow, GLFW.GLFW_KEY_LEFT);
		io.setKeyMap(ImGuiKey.RightArrow, GLFW.GLFW_KEY_RIGHT);
		io.setKeyMap(ImGuiKey.UpArrow, GLFW.GLFW_KEY_UP);
		io.setKeyMap(ImGuiKey.DownArrow, GLFW.GLFW_KEY_DOWN);
		io.setKeyMap(ImGuiKey.PageUp, GLFW.GLFW_KEY_PAGE_UP);
		io.setKeyMap(ImGuiKey.PageDown, GLFW.GLFW_KEY_PAGE_DOWN);
		io.setKeyMap(ImGuiKey.Home, GLFW.GLFW_KEY_HOME);
		io.setKeyMap(ImGuiKey.End, GLFW.GLFW_KEY_END);
		io.setKeyMap(ImGuiKey.Insert, GLFW.GLFW_KEY_INSERT);
		io.setKeyMap(ImGuiKey.Delete, GLFW.GLFW_KEY_DELETE);
		io.setKeyMap(ImGuiKey.Backspace, GLFW.GLFW_KEY_BACKSPACE);
		io.setKeyMap(ImGuiKey.Space, GLFW.GLFW_KEY_SPACE);
		io.setKeyMap(ImGuiKey.Enter, GLFW.GLFW_KEY_ENTER);
		io.setKeyMap(ImGuiKey.Escape, GLFW.GLFW_KEY_ESCAPE);
		io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW.GLFW_KEY_KP_ENTER);
		
		prevKeyCallback = GLFW.glfwSetKeyCallback(window.getWindowHandle(), (handle,key,scancode,action,mods)->{
			window.keyCallBack(key, action);
			if(!io.getWantCaptureKeyboard()) {
				if(prevKeyCallback != null) {
					prevKeyCallback.invoke(handle, key, scancode, action, mods);
				}
				return;
			}
			if(action == GLFW.GLFW_PRESS) {
				io.setKeysDown(key, true);
			}else if(action == GLFW.GLFW_RELEASE) {
				io.setKeysDown(key,false);
			}
			io.setKeyCtrl(io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL));
			io.setKeyShift(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT));
			io.setKeyAlt(io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT));
			io.setKeySuper(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER));
		});
		GLFW.glfwSetCharCallback(window.getWindowHandle(), (handle,c) -> {
			if(!io.getWantCaptureKeyboard()) {
				return;
			}
			io.addInputCharacter(c);
		});
	}
	public void render(Scene scene) {
		IGuiInstance guiInstance = scene.getGuiInstance();
		if(guiInstance == null) {
			return;
		}
		guiInstance.drawGui();
		
		shaderProgram.bind();
		GL11.glEnable(GL11.GL_BLEND);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		GL30.glBindVertexArray(guiMesh.getVaoId());
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, guiMesh.getVerticesVBO());
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, guiMesh.getIndicesVBO());
		
		ImGuiIO io = ImGui.getIO();
		scale.x = 2.0f / io.getDisplaySizeX();
		scale.y = -2.0f / io.getDisplaySizeY();
		uniformsMap.setUniform("scale", scale);
		
		ImDrawData drawData = ImGui.getDrawData();
		int numLists = drawData.getCmdListsCount();
		for(int i = 0;i < numLists;i++) {
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL15.GL_STREAM_DRAW);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL15.GL_STREAM_DRAW);
			
			int numCmds = drawData.getCmdListCmdBufferSize(i);
			for(int j = 0; j < numCmds; j++) {
				final int elemCount = drawData.getCmdListCmdBufferElemCount(i,j);
				final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
				final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;
				
				texture.bind();
				GL11.glDrawElements(GL11.GL_TRIANGLES, elemCount,GL11.GL_UNSIGNED_SHORT, indices);
			}
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	public void resize(int width, int height) {
		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setDisplaySize(width, height);
	}
}
