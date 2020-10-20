package io.msengine.client.graphics.shader;

import io.msengine.client.graphics.util.Symbol;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

public final class ShaderType extends Symbol {

    public static final ShaderType VERTEX = new ShaderType(GL_VERTEX_SHADER);
    public static final ShaderType FRAGMENT = new ShaderType(GL_FRAGMENT_SHADER);

    // Class //
    
    public ShaderType(int value) {
        super(value);
    }

}
