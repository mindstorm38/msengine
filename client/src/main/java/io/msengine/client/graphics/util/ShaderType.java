package io.msengine.client.graphics.util;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

public final class ShaderType {

    public static final ShaderType VERTEX = new ShaderType(GL_VERTEX_SHADER);
    public static final ShaderType FRAGMENT = new ShaderType(GL_FRAGMENT_SHADER);

    // Class //
    
    public final int value;

    public ShaderType(int value) {
        this.value = value;
    }

}
