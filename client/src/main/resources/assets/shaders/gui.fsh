#version 400

in vec4 out_color;
in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform vec4 global_color;
uniform int texture_enabled;

void main() {

	frag_color = mix(vec4(1.0), texture(texture_sampler, out_tex_coord), texture_enabled) * out_color * global_color;
	
}