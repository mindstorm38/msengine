package io.msengine.client.graphics.shader;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader implements AutoCloseable {

    private final ShaderType type;
    private int name = 0;

    public Shader(ShaderType type) {
        this.type = Objects.requireNonNull(type);
        this.name = glCreateShader(type.getValue());
        if (!glIsShader(this.name)) {
            throw new IllegalStateException("Failed to create the shader.");
        }
    }

    public ShaderType getType() {
        return this.type;
    }

    public int getName() {
        return this.name;
    }

    public boolean isValid() {
        return glIsShader(this.name);
    }

    public boolean isCompiled() {
        return glGetShaderi(this.name, GL_COMPILE_STATUS) == GL_TRUE;
    }

    public void checkValidity() {
        if (!this.isValid()) {
            throw new IllegalStateException("This shader is no longer usable, probably because of close.");
        }
    }

    public void checkNotCompiled() {
        if (this.isCompiled()) {
            throw new IllegalStateException("Cannot call this because the shader was already compiled.");
        }
    }

    public void checkCompiled() {
        if (!this.isCompiled()) {
            throw new IllegalStateException("Cannot call this since the shader is not compiled.");
        }
    }

    public void sendSource(String[] texts) {
        this.checkValidity();
        glShaderSource(this.name, texts);
    }

    public void sendSource(String text) {
        this.checkValidity();
        glShaderSource(this.name, text);
    }

    public void compile() {

        this.checkValidity();
        this.checkNotCompiled();

        glCompileShader(this.name);

        if (!this.isCompiled()) {
            throw new IllegalStateException("Failed to compile the shader: " + glGetShaderInfoLog(this.name));
        }

    }

    @Override
    public void close() {
        if (this.isValid()) {
            glDeleteShader(this.name);
            this.name = 0;
        }
    }

}
