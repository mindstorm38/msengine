#version 400

in vec4 out_color;
in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;

void main() {
	frag_color = vec4(out_color.rgb, out_color.a * texture(texture_sampler, out_tex_coord).r);
}