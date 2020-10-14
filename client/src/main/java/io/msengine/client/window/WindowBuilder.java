package io.msengine.client.window;

import static org.lwjgl.glfw.GLFW.*;

public abstract class WindowBuilder<W extends Window, SELF extends WindowBuilder<W, SELF>> {

    static {
        WindowHandler.init();
    }

    protected static int glfwBool(boolean b) {
        return b ? GLFW_TRUE : GLFW_FALSE;
    }

    // Class //

    protected int width = 1280;
    protected int height = 720;
    protected String title = "Hello world";
    protected long monitor = 0L;
    protected boolean resizable = false;
    protected boolean decorated = true;
    protected boolean floating = false;
    protected boolean transparentFramebuffer = false;

    protected void setupHints() {
        glfwWindowHint(GLFW_RESIZABLE, glfwBool(this.resizable));
        glfwWindowHint(GLFW_DECORATED, glfwBool(this.decorated));
        glfwWindowHint(GLFW_FLOATING, glfwBool(this.floating));
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, glfwBool(this.transparentFramebuffer));
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
    }

    protected long createWindow() {
        return glfwCreateWindow(this.width, this.height, this.title, this.monitor, 0L);
    }
    
    public W build() {

        if (!glfwInit()) {
            throw new IllegalStateException("Can't build a new window since GLFW failed to initialize.");
        }

        this.setupHints();

        long id = this.createWindow();

        if (id == 0L) {
            throw new IllegalStateException("Unable to create GLFW window.");
        }
        
        return this.create(id);

    }
    
    protected abstract W create(long id);

    // Attributes //
    
    @SuppressWarnings("unchecked")
    public SELF withSize(int width, int height) {
        this.width = width;
        this.height = height;
        return (SELF) this;
    }
    
    @SuppressWarnings("unchecked")
    public SELF withTitle(String title) {
        this.title = title;
        return (SELF) this;
    }
    
    @SuppressWarnings("unchecked")
    public SELF withMonitor(Monitor monitor) {
        this.monitor = monitor.getHandle();
        return (SELF) this;
    }
    
    @SuppressWarnings("unchecked")
    public SELF withResizable(boolean resizable) {
        this.resizable = resizable;
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    public SELF withDecorated(boolean decorated) {
        this.decorated = decorated;
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    public SELF withFloating(boolean floating) {
        this.floating = floating;
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    public SELF withTransparentFramebuffer(boolean transparentFramebuffer) {
        this.transparentFramebuffer = transparentFramebuffer;
        return (SELF) this;
    }
    
}