#version 400

in vec4 out_color;
in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform int texture_enabled;

void main() {

	frag_color = (texture_enabled * texture(texture_sampler, out_tex_coord) + abs(texture_enabled - 1.0)) * out_color;

}
