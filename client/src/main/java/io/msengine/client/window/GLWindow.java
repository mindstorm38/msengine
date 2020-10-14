package io.msengine.client.window;

import org.lwjgl.opengl.GLCapabilities;

public class GLWindow extends ContextWindow {

    public static final int MIN_VERSION_MAJOR = 4;
    public static final int MIN_VERSION_MINOR = 0;

    private GLCapabilities capabilities;
    
    GLWindow(long id, GLCapabilities capabilities) {
        super(id);
        this.capabilities = capabilities;
    }
    
    public GLCapabilities getCapabilities() {
        return this.capabilities;
    }
    
}
