package io.msengine.client.graphics.shader;

import io.msengine.client.graphics.shader.uniform.Uniform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.*;

/**
 * <p>A mid-level wrapper for OpenGL shader programs.</p>
 * <p>By using this, you should never delete the program
 * otherwise than calling {@link #close()}.</p>
 */
public class ShaderProgram implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger("shader.program");
    
    private List<Shader> shaders = new ArrayList<>();
    private int name;
    
    private List<ShaderComponent> components;

    public ShaderProgram() {
        this.name = glCreateProgram();
        if (!glIsProgram(this.name)) {
            throw new IllegalStateException("Failed to create shader program.");
        }
    }

    public int getName() {
        return this.name;
    }
    
    /**
     * @return True if this program is currently valid.
     */
    public boolean isValid() {
        return glIsProgram(this.name);
    }
    
    /**
     * @return True if this program is successfully linked, this
     * can also return false if the program is not valid.
     */
    public boolean isLinked() {
        return glGetProgrami(this.name, GL_LINK_STATUS) == GL_TRUE;
    }
    
    /**
     * @return True if this program is currently used in the render pipeline.
     */
    public boolean isUsed() {
        return glGetInteger(GL_CURRENT_PROGRAM) == this.name;
    }
    
    /**
     * @throws IllegalStateException If the program is no longer valid.
     */
    public void checkValidity() {
        if (!this.isValid()) {
            throw new IllegalStateException("This shader program is no longer usable, probably because of close.");
        }
    }
    
    /**
     * @throws IllegalStateException If the program is already linked, {@link #checkValidity()} is called before checking.
     */
    public void checkNotLinked() {
        this.checkValidity();
        if (this.isLinked()) {
            throw new IllegalStateException("Cannot call this because the program is already linked.");
        }
    }
    
    /**
     * @throws IllegalStateException If the program is not successfully linked.
     */
    public void checkLinked() {
        if (!this.isLinked()) {
            throw new IllegalStateException("Cannot call this because the program is not yet linked.");
        }
    }

    public void attachShader(Shader shader) {
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

        this.checkNotLinked();
        this.preLink();

        glLinkProgram(this.name);

        if (!this.isLinked()) {
            throw new IllegalStateException("Failed to link the shader program: " + glGetProgramInfoLog(this.name));
        }

        for (Shader shader : this.shaders) {
            glDetachShader(this.name, shader.getName());
        }
    
        try {
            this.postLink();
        } finally {
            this.shaders.clear();
            this.shaders = null;
        }

        glValidateProgram(this.name);

        if (glGetProgrami(this.name, GL_VALIDATE_STATUS) != GL_TRUE) {
            throw new IllegalStateException("Failed to validate the shader program: " + glGetProgramInfoLog(this.name));
        }

    }
    
    // For components //
    
    private List<ShaderComponent> getComponents() {
        if (this.components == null) {
            this.components = new ArrayList<>();
        }
        return this.components;
    }
    
    public <U extends Uniform> U createUniform(String identifier, Supplier<U> supplier) {
        
        this.checkLinked();
        
        int loc = glGetUniformLocation(this.name, identifier);
        
        if (loc == -1) {
            throw new IllegalArgumentException("Failed to find uniform '" + identifier + "'.");
        }
    
        U uniform = supplier.get();
        uniform.setup(this, identifier, loc);
        this.getComponents().add(uniform);
        return uniform;
        
    }
    
    // Using //
    
    public void use() {
        this.checkLinked();
        glUseProgram(this.name);
    }
    
    public static void release() {
        glUseProgram(0);
    }
    
    // Closing //

    @Override
    public void close() {
        
        if (glIsProgram(this.name)) {
            glDeleteProgram(this.name);
            this.name = 0;
        }
        
        if (this.components != null) {
            this.components.forEach(ShaderProgram::closeComponent);
            this.components = null;
        }
        
    }
    
    private static void closeComponent(ShaderComponent component) {
        try {
            component.close();
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Failed to close a shader component.", e);
        }
    }
    
}
