#version 330 core

in vec2 ioTexCoords;

uniform int uType;
uniform float uOpacity;
uniform vec3 uColor;
uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0: //Used to render font.
            ioResult = vec4(uColor, texture(uTexture, ioTexCoords).a);
            break;
        
        case 1: //Used to render background rectangle.
            ioResult = vec4(uColor, uOpacity);
            break;
    }
}