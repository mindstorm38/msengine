#version 400

in vec4 out_color;
in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform vec4 global_color;

void main() {
	frag_color = out_color * global_color * texture(texture_sampler, out_tex_coord).r;
}