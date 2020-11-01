package io.msengine.client.window;

import io.msengine.client.window.listener.WindowCharEventListener;
import io.msengine.client.window.listener.WindowFramebufferSizeEventListener;
import io.msengine.client.window.listener.WindowKeyEventListener;
import io.msengine.client.window.listener.WindowMouseButtonEventListener;
import io.msengine.client.window.listener.WindowMousePositionEventListener;
import io.msengine.client.window.listener.WindowScrollEventListener;
import io.msengine.common.util.event.MethodEventManager;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;

public abstract class Window implements AutoCloseable {

    protected static final Logger LOGGER = Logger.getLogger("mse.window");

    static {
        WindowHandler.init();
    }
    
    public static final int ACTION_PRESS = GLFW_PRESS;
    public static final int ACTION_RELEASE = GLFW_RELEASE;
    public static final int ACTION_REPEAT = GLFW_REPEAT;
    
    public static final int MOD_SHIFT = GLFW_MOD_SHIFT;
    public static final int MOD_CTRL = GLFW_MOD_CONTROL;
    public static final int MOD_ALT = GLFW_MOD_ALT;
    public static final int MOD_SUPER = GLFW_MOD_SUPER;
    public static final int MOD_CAPS_LOCK = GLFW_MOD_CAPS_LOCK;
    public static final int MOD_NUM_LOCK = GLFW_MOD_NUM_LOCK;
    
    public static final int MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_MIDDLE;
    public static final int MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    
    // Class //

    protected long id;
    protected int fbWidth, fbHeight;
    
    private final MethodEventManager eventManager = new MethodEventManager();

    Window(long id) {

        this.id = id;

        this.eventManager.addAllowedClass(WindowCharEventListener.class);
        this.eventManager.addAllowedClass(WindowFramebufferSizeEventListener.class);
        this.eventManager.addAllowedClass(WindowKeyEventListener.class);
        this.eventManager.addAllowedClass(WindowMouseButtonEventListener.class);
        this.eventManager.addAllowedClass(WindowMousePositionEventListener.class);
        this.eventManager.addAllowedClass(WindowScrollEventListener.class);

        glfwSetKeyCallback(id, (long window, int key, int scanCode, int action, int mods) -> {
            this.eventManager.fireListeners(WindowKeyEventListener.class, l -> l.onWindowKeyEvent(this, key, scanCode, action, mods));
        });

        glfwSetMouseButtonCallback(id, (long window, int button, int action, int mods) -> {
            this.eventManager.fireListeners(WindowMouseButtonEventListener.class, l -> l.onWindowMouseButtonEvent(this, button, action, mods));
        });

        glfwSetScrollCallback(id, (long window, double xOffset, double yOffset) -> {
            this.eventManager.fireListeners(WindowScrollEventListener.class, l -> l.onWindowScrollEvent(this, xOffset, yOffset));
        });

        glfwSetCursorPosCallback(id, (long window, double x, double y) -> {
            int ix = (int) x;
            int iy = (int) y;
            this.eventManager.fireListeners(WindowMousePositionEventListener.class, l -> l.onWindowMousePositionEvent(this, ix, iy));
        });

        glfwSetCharCallback(id, (long window, int codePoint) -> {
            this.eventManager.fireListeners(WindowCharEventListener.class, l -> l.onWindowCharEvent(this, codePoint));
        });

        glfwSetFramebufferSizeCallback(id, (long window, int width, int height) -> {
            this.fbWidth = width;
            this.fbHeight = height;
            this.eventManager.fireListeners(WindowFramebufferSizeEventListener.class, l -> l.onWindowFramebufferSizeChangedEvent(this, width, height));
        });

    }

    public long getId() {
        return this.id;
    }

    public boolean isValid() {
        return this.id != 0L;
    }
    
    public long checkId() {
        if (this.id == 0L) {
            throw new IllegalStateException("Can't call this method because the " + this.getClass().getSimpleName() + " is already closed.");
        }
        return this.id;
    }

    public MethodEventManager getEventManager() {
        return this.eventManager;
    }

    // Real methods //
    
    public void setSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        glfwSetWindowSizeLimits(this.checkId(), minWidth, minHeight, maxWidth, maxHeight);
    }
    
    public void show() {
        glfwShowWindow(this.checkId());
    }
    
    public void hide() {
        glfwHideWindow(this.checkId());
    }
    
    public void setVisible(boolean visible) {
        if (visible) {
            this.show();
        } else {
            this.hide();
        }
    }
    
    public int getFramebufferWidth() {
        return this.fbWidth;
    }
    
    public int getFramebufferHeight() {
        return this.fbHeight;
    }
    
    public void getFramebufferSize(SizeConsumer consumer) {
        consumer.accept(this.fbWidth, this.fbHeight);
        /*this.checkId();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(this.id, width, height);
            consumer.accept(width.get(), height.get());
        }*/
    }
    
    public void getCursorPos(CursorPosConsumer consumer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.mallocDouble(1);
            DoubleBuffer y = stack.mallocDouble(1);
            glfwGetCursorPos(this.checkId(), x, y);
            consumer.accept(x.get(0), y.get(0));
        }
    }
    
    public void setFullscreen(Monitor monitor) {
        
        GLFWVidMode vidMode = monitor.getVideoMode();
        
        if (vidMode == null) {
            LOGGER.warning("Can't set window to fullscreen on monitor '" + monitor.getName() + "' because no video mode is available.");
            return;
        }
        
        glfwSetWindowMonitor(this.checkId(), monitor.getHandle(), 0, 0, vidMode.width(), vidMode.height(), GLFW_DONT_CARE);
        
    }
    
    public void setFullscreen() {
        this.setFullscreen(Monitor.getPrimaryMonitor());
    }
    
    public boolean isFullscreen() {
        return glfwGetWindowMonitor(this.checkId()) != 0L;
    }
    
    public void setMaximized() {
        glfwMaximizeWindow(this.checkId());
    }
    
    public boolean isMaximized() {
        return glfwGetWindowAttrib(this.checkId(), GLFW_MAXIMIZED) == GLFW_TRUE;
    }
    
    public void setRestored() {
        glfwRestoreWindow(this.checkId());
    }
    
    public boolean isResizable() {
        return glfwGetWindowAttrib(this.checkId(), GLFW_RESIZABLE) == GLFW_TRUE;
    }

    public boolean shouldClose() {
        return this.id != 0L && glfwWindowShouldClose(this.id);
    }

    @Override
    public void close() {
        if (this.id != 0L) {
            glfwFreeCallbacks(this.id);
            glfwDestroyWindow(this.id);
            this.id = 0L;
        }
    }

    public static void pollEvents() {
        glfwPollEvents();
    }

    public static double getTime() {
        return glfwGetTime();
    }
    
    public static boolean hasModifier(int modifiers, int target) {
        return (modifiers & target) == target;
    }
    
    @FunctionalInterface
    public interface SizeConsumer {
        void accept(int width, int height);
    }
    
    @FunctionalInterface
    public interface CursorPosConsumer {
        void accept(double x, double y);
    }

}
