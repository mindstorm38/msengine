package io.msengine.client.graphics.shader;

import io.msengine.client.graphics.shader.uniform.Uniform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements AutoCloseable {

    private List<Shader> shaders = new ArrayList<>();
    private int name;

    public ShaderProgram() {
        this.name = glCreateProgram();
        if (!glIsProgram(this.name)) {
            throw new IllegalStateException("Failed to create shader program.");
        }
    }

    public int getName() {
        return this.name;
    }

    public boolean isValid() {
        return glIsProgram(this.name);
    }

    public boolean isLinked() {
        return glGetProgrami(this.name, GL_LINK_STATUS) == GL_TRUE;
    }

    public void checkValidity() {
        if (!this.isValid()) {
            throw new IllegalStateException("This shader program is no longer usable, probably because of close.");
        }
    }

    public void checkNotLinked() {
        if (this.isLinked()) {
            throw new IllegalStateException("Cannot call this because the program is already linked.");
        }
    }
    
    public void checkLinked() {
        if (!this.isLinked()) {
            throw new IllegalStateException("Cannot call this because the program is not yet linked.");
        }
    }

    public void attachShader(Shader shader) {
        this.checkValidity();
        this.checkNotLinked();
        shader.checkCompiled();
        this.shaders.add(shader);
        glAttachShader(this.name, shader.getName());
    }
    
    /**
     * @return Attached shaders, or null if this program is already linked.
     */
    public List<Shader> getAttachedShaders() {
        return this.shaders;
    }
    
    /**
     * Delete attached shaders (call {@link Shader#close()},
     * this can be called before linking, or at the latest in
     * {@link #postLink()} method.
     */
    public void deleteAttachedShaders() {
        if (this.shaders != null) {
            this.shaders.forEach(Shader::close);
        }
    }
    
    protected void preLink() { }
    protected void postLink() { }

    public void link() {

        this.checkValidity();
        this.checkNotLinked();
        this.preLink();

        glLinkProgram(this.name);

        if (!this.isLinked()) {
            throw new IllegalStateException("Failed to link the shader program: " + glGetProgramInfoLog(this.name));
        }

        for (Shader shader : this.shaders) {
            glDetachShader(this.name, shader.getName());
        }
    
        this.postLink();

        this.shaders.clear();
        this.shaders = null;

        glValidateProgram(this.name);

        if (glGetProgrami(this.name, GL_VALIDATE_STATUS) != GL_TRUE) {
            throw new IllegalStateException("Failed to validate the shader program: " + glGetProgramInfoLog(this.name));
        }

    }
    
    public <U extends Uniform> U getUniformLocation(String identifier, Supplier<U> supplier) {
        
        this.checkValidity();
        this.checkLinked();
        
        int loc = glGetUniformLocation(this.name, identifier);
        
        if (loc == -1) {
            throw new IllegalArgumentException("Failed to find uniform '" + identifier + "'.");
        }
    
        U uniform = supplier.get();
        uniform.setup(identifier, loc);
        return uniform;
        
    }

    @Override
    public void close() {
        if (glIsProgram(this.name)) {
            glDeleteProgram(this.name);
            this.name = 0;
        }
    }

}
