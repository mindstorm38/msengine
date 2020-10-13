package io.msengine.client.window;

import static org.lwjgl.glfw.GLFW.*;

public class GLWindow extends Window {

    public static final int MIN_VERSION_MAJOR = 4;
    public static final int MIN_VERSION_MINOR = 0;

    GLWindow(long id, int width, int height) {
        super(id, width, height);
    }

    public static class GLBuilder extends Window.Builder<GLWindow, GLBuilder> {

        private int versionMajor = MIN_VERSION_MAJOR;
        private int versionMinor = MIN_VERSION_MINOR;

        GLBuilder() {}

        @Override
        protected void setupHints() {
            super.setupHints();
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, this.versionMajor);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, this.versionMinor);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        protected GLWindow create(long id) {
            return new GLWindow(id, this.width, this.height);
        }

        // Attributes //

        public Builder withVersion(int major, int minor) {
            this.versionMajor = major;
            this.versionMinor = minor;
            return this;
        }

    }

}
