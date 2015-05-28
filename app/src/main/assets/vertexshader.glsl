uniform mat4 view;
uniform mat4 model;
uniform mat4 proj;

attribute vec3 in_pos;
attribute vec2 in_texture;
varying vec2 textureCoords;

void main() {
    textureCoords = in_texture;
    gl_Position = proj * (view * model) * vec4(in_pos,1.0);
}