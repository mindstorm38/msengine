package io.msengine.client.window;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class WindowHandler {

    protected static final Logger LOGGER = Logger.getLogger("mse.window");

    private WindowHandler() {}

    static {
        registerErrorCallback();
    }

    /**
     * Empty init method to trigger the static block.
     */
    public static void init() {}

    public static void registerErrorCallback() {
        setErrorCallback(WindowHandler::errorCallback);
    }
    
    public static void ensureReady() {
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW.");
        }
    }

    public static void terminateWindows() {
        glfwTerminate();
        setErrorCallback(null);
    }

    // Internal //

    private static void setErrorCallback(GLFWErrorCallbackI callback) {
        GLFWErrorCallback cb = glfwSetErrorCallback(callback);
        if (cb != null) {
            cb.free();
        }
    }

    private static void errorCallback(int error, long description) {
        LOGGER.log(Level.SEVERE, "[GLFW] Error: " + MemoryUtil.memASCII(description) + " (" + error + ")");
    }

}
