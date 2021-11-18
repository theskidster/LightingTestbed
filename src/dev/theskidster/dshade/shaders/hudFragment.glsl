#version 330 core

in vec2 ioTexCoords;

uniform int uType;
uniform vec3 uColor;
uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0:
            ioResult = vec4(uColor, texture(uTexture, ioTexCoords).a);
            break;
    }
}