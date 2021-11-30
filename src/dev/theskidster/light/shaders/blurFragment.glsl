#version 330 core

in vec2 ioTexCoords;

uniform int uHorizontal;
uniform float uWeight[5] = float[] (0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162);
uniform sampler2D uTexture;

out vec4 ioResult;

void main() {
    vec2 texOffset = 1.0 / textureSize(uTexture, 0);
    vec3 result    = texture(uTexture, ioTexCoords).rgb * uWeight[0];
    
    if(uHorizontal == 1) {
        for(int i = 1; i < 5; ++i) {
            result += texture(uTexture, ioTexCoords + vec2(texOffset.x * i, 0.0)).rgb * uWeight[i];
            result += texture(uTexture, ioTexCoords - vec2(texOffset.x * i, 0.0)).rgb * uWeight[i];
        }
    } else {
        for(int i = 1; i < 5; ++i) {
            result += texture(uTexture, ioTexCoords + vec2(0.0, texOffset.y * i)).rgb * uWeight[i];
            result += texture(uTexture, ioTexCoords - vec2(0.0, texOffset.y * i)).rgb * uWeight[i];
        }
    }
    
    ioResult = vec4(result, 1.0);
}