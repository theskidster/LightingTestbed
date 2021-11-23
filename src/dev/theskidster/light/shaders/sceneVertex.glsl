#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in vec3 aNormal;

uniform int uType;
uniform vec2 uTexCoords;
uniform vec3 uColor;
uniform mat3 uNormal;
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uLightSpace;

out vec2 ioTexCoords;
out vec3 ioColor;
out vec3 ioNormal;
out vec3 ioFragPos;
out vec4 ioLightFrag;

void main() {
    switch(uType) {        
        case 0: case 1: //Used to render planes and cubes.
            ioColor     = uColor;
            ioNormal    = uNormal * aNormal;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            ioLightFrag = uLightSpace * vec4(ioFragPos, 1);
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
        
        case 2: //Used to render light source icons.
            ioTexCoords = aTexCoords + uTexCoords;
            ioColor     = uColor;
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
        
        case 3: //Used to render 3D models.
            ioTexCoords = aTexCoords;
            ioColor     = uColor;
            ioNormal    = uNormal * aNormal;
            ioFragPos   = vec3(uModel * vec4(aPosition, 1));
            ioLightFrag = uLightSpace * vec4(ioFragPos, 1);
            gl_Position = uProjection * uView * uModel * vec4(aPosition, 1);
            break;
    }
}