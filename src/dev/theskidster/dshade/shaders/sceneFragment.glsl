#version 330 core

in vec3 ioColor;
in vec3 ioNormal;

uniform int uType;

out vec4 ioResult;

void main() {
    switch(uType) {
        case 0: //Used to render planes.
            ioResult = vec4(ioColor, 1);
            break;
        
        case 1: //Used to render cubes.
            ioResult = vec4(ioColor, 1);
            break;
    }
}