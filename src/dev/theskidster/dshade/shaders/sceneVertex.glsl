#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 3) in vec3 aNormal;

uniform int uType;
uniform vec3 uColor;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

out vec3 ioColor;
out vec3 ioNormal;

void main() {
    switch(uType) {
        case 0: //Used to render planes.
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
        
        case 1: //Used to render cubes.
            ioColor     = uColor;
            ioNormal    = aNormal;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}