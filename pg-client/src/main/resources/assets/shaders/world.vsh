#version 400

layout(location=0) in vec3 position;
layout(location=1) in vec3 color;
layout(location=2) in vec2 tex_coord;

out vec3 out_color;
out vec2 out_tex_coord;

uniform mat4 global_matrix;  // This matrix is the product of the project matrix and the view matrix.
uniform mat4 model_matrix;   // Model matrix, requently updated, so set apart from global_matrix.
uniform vec3 global_offset;  // Offset only for coordinates (not view rotation), fixme: probably useless in the future.

void main() {

    gl_Position = global_matrix * model_matrix * vec4(position + global_offset, 1.0);

    out_color = color;
    out_tex_coord = tex_coord;

}