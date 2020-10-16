package io.msengine.client.graphics.shader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader implements AutoCloseable {

    public static Shader fromSource(ShaderType type, InputStream stream, Charset charset) throws IOException {
        Shader shader = new Shader(type);
        shader.sendSource(stream, charset);
        return shader;
    }
    
    public static Shader fromSource(ShaderType type, InputStream stream) throws IOException {
        return fromSource(type, stream, Charset.defaultCharset());
    }
    
    // Class //
    
    private final ShaderType type;
    private int name;

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
    
    public void sendSource(InputStream stream, Charset charset) throws IOException {
        
        Objects.requireNonNull(stream);
        Objects.requireNonNull(charset);
        
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        final byte[] buf = new byte[512];
        int len;
        while ((len = stream.read(buf)) != -1) {
            res.write(buf, 0, len);
        }
        
        this.sendSource(res.toString(charset.name()));
        
    }
    
    public void sendSource(InputStream stream) throws IOException {
        this.sendSource(stream, Charset.defaultCharset());
    }
    
    protected void preCompile() { }

    public void compile() {

        this.checkValidity();
        this.checkNotCompiled();
        this.preCompile();

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
