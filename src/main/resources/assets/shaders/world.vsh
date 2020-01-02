#version 400

layout(location=0) in vec3 position;
layout(location=1) in vec3 color;
layout(location=2) in vec2 tex_coord;

out vec3 out_color;
out vec2 out_tex_coord;

uniform mat4 global_matrix;
uniform vec2 global_offset;

void main() {

    gl_Position = global_matrix * vec4(position.x + global_offset.x, position.y, position.z + global_offset.y, 1.0);

    out_color = color;
    out_tex_coord = tex_coord;

}