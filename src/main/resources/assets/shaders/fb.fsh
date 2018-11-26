#version 400

in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform vec2 resolution;
uniform float time;

void main() {
	
	frag_color = texture( texture_sampler, out_tex_coord );
	
}