#version 400

in vec3 out_color;
in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform int texture_enabled;

void main() {

	frag_color = mix(vec4(1.0), texture(texture_sampler, out_tex_coord), texture_enabled) * vec4(out_color, 1.0);

}
