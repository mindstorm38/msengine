package io.msengine.client.graphics.shader;

import org.lwjgl.opengl.GL20;

public final class ShaderType {

    public static final ShaderType VERTEX = new ShaderType(GL20.GL_VERTEX_SHADER);
    public static final ShaderType FRAGMENT = new ShaderType(GL20.GL_FRAGMENT_SHADER);

    private final int value;

    public ShaderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
