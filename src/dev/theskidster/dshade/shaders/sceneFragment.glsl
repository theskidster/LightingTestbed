#version 330 core

in vec3 ioColor;

out vec4 ioResult;

void main() {
    ioResult = vec4(ioColor, 1);
}