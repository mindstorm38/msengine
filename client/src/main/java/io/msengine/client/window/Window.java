package io.msengine.client.window;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public abstract class Window {

    protected static final Logger LOGGER = Logger.getLogger("msengine.window");

    // Class //

    protected long id;
    private int realWidth;
    private int realHeight;
    //private int minimizedWidth;
    //private int minimizedHeight;
    //private boolean fullscreen = false;

    Window(long id, int width, int height) {
        this.id = id;
        this.realWidth = width;
        this.realHeight = height;
        //this.minimizedWidth = width;
        //this.minimizedHeight = height;
    }

    public long getId() {
        return this.id;
    }
    
    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        glfwSetWindowSizeLimits(this.id, minWidth, minHeight, maxWidth, maxHeight);
    }
    
    public void show() {
        glfwShowWindow(this.id);
    }
    
    public void hide() {
        glfwHideWindow(this.id);
    }
    
    public void setVisible(boolean visible) {
        if (visible) {
            this.show();
        } else {
            this.hide();
        }
    }
    
    public void getSize(BiConsumer<Integer, Integer> consumer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            consumer.accept(width.get(), height.get());
        }
    }
    
    public void setFullscreen(Monitor monitor) {
        
        GLFWVidMode vidMode = monitor.getVideoMode();
        
        if (vidMode == null) {
            LOGGER.warning("Can't set window to fullscreen on monitor '" + monitor.getName() + "'.");
            return;
        }
    
        /*if (!this.fullscreen) {
            this.getSize((width, height) -> {
                this.minimizedWidth = width;
                this.minimizedHeight = height;
            });
        }*/
        
        glfwSetWindowMonitor(this.id, monitor.getHandle(), 0, 0, vidMode.width(), vidMode.height(), GLFW_DONT_CARE);
        
    }
    
    public void setFullscreen() {
        this.setFullscreen(Monitor.getPrimaryMonitor());
    }
    
    public boolean isFullscreen() {
        return glfwGetWindowMonitor(this.id) != 0L;
    }
    
    public void setMaximized() {
        glfwMaximizeWindow(this.id);
    }
    
    public boolean isMaximized() {
        return glfwGetWindowAttrib(this.id, GLFW_MAXIMIZED) == GLFW_TRUE;
    }
    
    public void setRestored() {
        glfwRestoreWindow(this.id);
    }
    
    public boolean isResizable() {
        return glfwGetWindowAttrib(this.id, GLFW_RESIZABLE) == GLFW_TRUE;
    }

}
