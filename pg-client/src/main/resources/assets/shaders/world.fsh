#version 400

in vec3 out_color;
in vec2 out_tex_coord;

layout(location=0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform int texture_enabled;

uniform vec4 global_color;

uniform int fog_enabled;

const vec4 fog_color = vec4(0.8, 0.8, 0.8, 1.0);
const float fog_density = 0.2;

const vec4 one = vec4(1.0);

void main() {

	frag_color = mix(one, texture(texture_sampler, out_tex_coord), texture_enabled) * vec4(out_color, 1.0) * global_color;

	if (frag_color.a < 0.5)
		discard;

	/*
	float depth = texture( texture_sampler, out_tex_coord.xy).r;
	float depth2 = texture( texture_sampler, out_tex_coord.xy + vec2(0,0.002)).r;
	float depth3 = texture( texture_sampler, out_tex_coord.xy + vec2(0.002,0)).r;

	float fac = abs(depth-depth2) + abs(depth-depth3);

	float intensity = 9050;

	vec4 sub = vec4(vec3(fac) * intensity,0);

	frag_color += sub;
	*/

	// Compute fog
	/*
	if (fog_enabled == 1) {

		float dist = (gl_FragCoord.z / gl_FragCoord.w);
		float fog_factor = 1.0 / exp(dist * fog_density - 32.0);
		fog_factor = clamp(fog_factor, 0.0, 1.0);

		frag_color = mix(fog_color, frag_color, fog_factor);

	}
	*/

}