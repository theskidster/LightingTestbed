#version 330 core

#define MAX_LIGHTS 32

in vec2 ioTexCoords;
in vec3 ioColor;
in vec3 ioNormal;
in vec3 ioFragPos;
in vec4 ioLightFrag;

struct Light {
    float brightness;
    float contrast;
    float distance;
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform int uType;
uniform int uNumLights;
uniform int uPCFValue;
uniform int uShine;
uniform vec3 uCamPos;
uniform sampler2D uTexture;
uniform sampler2D uShadowMap;
uniform sampler2D uBloomTexture;
uniform Light uLights[MAX_LIGHTS];

layout (location = 0) out vec4 ioFragColor;
layout (location = 1) out vec4 ioBrightColor;

float calcShadow(float dotLightNormal) {
    vec3 pos = ioLightFrag.xyz * 0.5 + 0.5;

    if(pos.z > 1) pos.z = 1;
    
    float depth = texture(uShadowMap, pos.xy).r;
    float bias  = max(0.0009 * (1 - dotLightNormal), 0.00003);
    
    if(uPCFValue > 0) {
        vec2 texelSize = 1.0 / textureSize(uShadowMap, 0);
        float pcfValue = 0;
        float factor   = 1 / (uPCFValue * 2.0 + 1.0);

        for(int x = -uPCFValue; x <= uPCFValue; x++) {
            for(int y = -uPCFValue; y <= uPCFValue; y++) {
                float pcfDepth = texture(uShadowMap, pos.xy + vec2(x, y) * texelSize).r; 

                if(pos.z + bias < pcfDepth) {
                    pcfValue += factor;
                }
            }
        }

        if(pcfValue > 1) pcfValue = 1;

        return pcfValue;
    } else {
        return (depth + bias) < pos.z ? 0 : 1;
    }
}

vec3 calcWorldLight(Light light, vec3 normal) {
    vec3 lightDir = normalize(light.position);
    
    float diff   = max(dot(normal, lightDir), 0);
    vec3 diffuse = diff * uLights[0].diffuse * uLights[0].brightness;
    vec3 ambient = uLights[0].ambient * (1 - uLights[0].contrast);
    
    vec3 cameraDir  = normalize(uCamPos - ioFragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec      = pow(max(dot(cameraDir, reflectDir), 0), uShine);
    vec3 specular   = spec * light.specular;
    
    float dotLightNormal = dot(lightDir, normal);
    float shadow         = calcShadow(dotLightNormal);
    
    vec3 lighting = (uShine != 0) 
                  ? (shadow * diffuse + ambient + specular) * ioColor 
                  : (shadow * diffuse + ambient) * ioColor;
    
    return lighting;
}

vec3 calcPointLight(Light light, vec3 normal, vec3 fragPos) {
    vec3 lightDir = normalize(light.position - ioFragPos);
    
    float diff   = max(dot(normal, lightDir), 0);
    vec3 ambient = light.ambient * (1 - light.contrast);
    vec3 diffuse = diff * light.diffuse * light.brightness;
    
    float linear    = 0.14f / light.distance;
    float quadratic = 0.07f / light.distance;
    float dist      = length(light.position - ioFragPos);
    float attenuate = 1.0f / (1.0f + linear * dist + quadratic * (dist * dist));
    
    ambient *= attenuate;
    diffuse *= attenuate;
    
    vec3 cameraDir  = normalize(uCamPos - ioFragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec      = pow(max(dot(cameraDir, reflectDir), 0), uShine);
    vec3 specular   = spec * light.specular;
    
    vec3 lighting = (uShine != 0) 
                  ? (diffuse + ambient + specular) * ioColor 
                  : (diffuse + ambient) * ioColor;
    
    return lighting;
}

void main() {
    switch(uType) {        
        case 0: case 1: //Used to render planes and cubes.
            vec3 lighting = calcWorldLight(uLights[0], normalize(ioNormal));
            
            for(int i = 1; i < uNumLights; i++) {
                lighting += calcPointLight(uLights[i], normalize(ioNormal), ioFragPos);
            }
            
            ioFragColor = vec4(lighting * ioColor, 1);
            break;
        
        case 2: //Used to render light source icons.
            ioFragColor = texture(uTexture, ioTexCoords) * vec4(ioColor, texture(uTexture, ioTexCoords).a);
            break;
        
        case 3: //Used to render 3D models.
            vec3 lighting2 = calcWorldLight(uLights[0], normalize(ioNormal));
            
            for(int i = 1; i < uNumLights; i++) {
                lighting2 += calcPointLight(uLights[i], normalize(ioNormal), ioFragPos);
            }
            
            ioFragColor = texture(uTexture, ioTexCoords) * vec4(lighting2 * ioColor, 1);
            break;
        
        case 4: //Used for the viewport framebuffer.
            vec3 sceneColor = texture(uTexture, ioTexCoords).rgb;
            sceneColor += texture(uBloomTexture, ioTexCoords).rgb;
            
            ioFragColor = vec4(sceneColor, 1);
            break;
        
        case 5: //Used for rendering the bloom test entity.
            ioFragColor = vec4(ioColor, 1);
            break;
    }
    
    float brightness = dot(ioFragColor.rgb, vec3(0.2126, 0.7152, 0.0722));
    ioBrightColor    = (brightness > 1.0) ? vec4(ioFragColor.rgb, 1) : vec4(0, 0, 0, 1);
}