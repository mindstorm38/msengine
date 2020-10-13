package io.msengine.client.window;

import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class Window {

    protected static final Logger LOGGER = Logger.getLogger("msengine.window");

    public static NoAPIBuilder forNoAPI() {
        return new NoAPIBuilder();
    }

    public static GLWindow.GLBuilder forOpenGL() {
        return new GLWindow.GLBuilder();
    }

    // Class //

    private final long id;

    private int realWidth;
    private int realHeight;
    private int minimizedWidth;
    private int minimizedHeight;

    Window(long id, int width, int height) {

        this.id = id;

        this.realWidth = width;
        this.realHeight = height;
        this.minimizedWidth = width;
        this.minimizedHeight = height;

    }

    public long getId() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public static abstract class Builder<W extends Window, B extends Builder<W, B>> {

        protected int width = 1280;
        protected int height = 720;
        protected String title = "Hello world";
        protected long monitor = 0L;
        protected long share = 0L;
        protected boolean resizable = false;

        Builder() {}

        protected void setupHints() {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_VISIBLE, this.resizable ? GLFW_TRUE : GLFW_FALSE);
        }

        public W build() {

            if (!glfwInit()) {
                throw new IllegalStateException("Can't build a new window since GLFW failed to initialize.");
            }

            this.setupHints();

            long id = glfwCreateWindow(this.width, this.height, this.title, this.monitor, this.share);

            if (id == 0L) {
                throw new IllegalStateException("Unable to create GLFW window !");
            }

            return this.create(id);

        }

        protected abstract W create(long id);

        // Attributes //

        public B withSize(int width, int height) {
            this.width = width;
            this.height = height;
            return (B) this;
        }

        public B withTitle(String title) {
            this.title = title;
            return (B) this;
        }

        public B shareOther(Window otherWindow) {
            this.share = otherWindow.id;
            return (B) this;
        }

        public B withResizable(boolean resizable) {
            this.resizable = resizable;
            return (B) this;
        }

    }

    public static class NoAPIBuilder extends Builder<Window, NoAPIBuilder> {

        NoAPIBuilder() {}

        protected Window create(long id) {
            return new Window(id, this.width, this.height);
        }

    }

}
