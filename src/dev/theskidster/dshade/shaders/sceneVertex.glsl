#version 330 core

layout (location = 0) in vec3 aPosition;

uniform vec3 uColor;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec3 ioColor;

void main() {
    ioColor     = uColor;
    gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
}