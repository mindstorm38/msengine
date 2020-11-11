#version 400

layout(location=0) in vec2 position;

uniform mat4 projection_matrix;
uniform mat4 model_matrix;

void main() {
    gl_Position = projection_matrix * model_matrix * vec4(position, 0.0, 1.0);
}