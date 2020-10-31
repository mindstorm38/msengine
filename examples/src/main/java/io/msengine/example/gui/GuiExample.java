package io.msengine.example.gui;

import io.msengine.client.graphics.gui.GuiManager;
import io.msengine.client.graphics.util.DebugOutput;
import io.msengine.client.window.GLWindow;
import io.msengine.client.window.GLWindowBuilder;
import io.msengine.common.logic.FrameRegulated;
import io.msengine.common.util.GameLoggerFormatter;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

public class GuiExample implements FrameRegulated {
	
	private static final Logger LOGGER = Logger.getLogger("example.gui");
	
	public static void main(String[] args) {
		
		GuiExample ex = new GuiExample();
		ex.init();
		ex.start();
		ex.stop();
		
	}
	
	private GLWindow window;
	private GuiManager gui;
	
	private GuiExample() { }
	
	private void init() {
		
		GameLoggerFormatter.setupRootLogger();
		Logger.getLogger("").setLevel(Level.WARNING);
		
		this.window = new GLWindowBuilder()
				.withTitle("[MSE] GUI Example")
				.withVersion(4, 3)
				.build();
		
		this.gui = new GuiManager(this.window);
		this.gui.registerScene("test", GuiTestScene::new);
		
		this.window.show();
		this.window.makeContextCurrent();
		
		DebugOutput.registerIfSupported();
		
		this.gui.init();
		this.gui.loadScene("test");
		
		glClearColor(0, 0, 0, 1);
		
	}
	
	private void start() {
		this.regulateFrames(10, 20);
	}
	
	private void stop() {
		
		this.gui.stop();
		this.window.close();
		this.window = null;
	
	}
	
	@Override
	public void render(float alpha) {
		
		GLWindow.pollEvents();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
		
		this.gui.render(alpha);
		this.window.swapBuffers();
		
	}
	
	@Override
	public void tick() {
		this.gui.update();
	}
	
	@Override
	public boolean mustSync() {
		return true;
	}
	
	@Override
	public boolean shouldStop() {
		return this.window.shouldClose();
	}
	
}
