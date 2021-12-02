#version 330 core

in vec2 ioTexCoords;

layout (location = 0) out vec4 ioFragColor;

uniform float uWeight[5] = float[] (0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162);
uniform sampler2D uBloomTexture;

void main() {
    vec2 texOffset = 6.0 / textureSize(uBloomTexture, 0);
    vec3 result    = texture(uBloomTexture, ioTexCoords).rgb * uWeight[0];

    for(int i = 1; i < 5; ++i) {
        result += texture(uBloomTexture, ioTexCoords + vec2(texOffset.x * i, 0.0)).rgb * uWeight[i];
        result += texture(uBloomTexture, ioTexCoords - vec2(texOffset.x * i, 0.0)).rgb * uWeight[i];
    }

    for(int i = 1; i < 5; ++i) {
        result += texture(uBloomTexture, ioTexCoords + vec2(0.0, texOffset.y * i)).rgb * uWeight[i];
        result += texture(uBloomTexture, ioTexCoords - vec2(0.0, texOffset.y * i)).rgb * uWeight[i];
    }

    ioFragColor = texture(uBloomTexture, ioTexCoords);
}