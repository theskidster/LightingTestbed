#version 330 core

in vec2 ioTexCoords;
in vec3 ioColor;
in vec3 ioNormal;

uniform int uType;

uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0: //Used to render planes.
            ioResult = vec4(ioColor, 1);
            break;
        
        case 1: //Used to render cubes.
            ioResult = vec4(ioColor, 1);
            break;
        
        case 2: //Used to render light source icons.
            ioResult = texture(uTexture, ioTexCoords) * vec4(ioColor, texture(uTexture, ioTexCoords).a);
            break;
    }
}