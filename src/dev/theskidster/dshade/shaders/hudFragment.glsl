#version 330 core

in vec2 ioTexCoords;

uniform int uType;
uniform vec3 uColor;
uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0:
            vec4 sampled = vec4(1, 1, 1, texture(uTexture, ioTexCoords).r);
            ioResult     = vec4(uColor, 1) * sampled;
            break;
    }
}