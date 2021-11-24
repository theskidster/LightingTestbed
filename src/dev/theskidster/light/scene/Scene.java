package dev.theskidster.light.scene;

import dev.theskidster.jlogger.JLogger;
import dev.theskidster.light.entity.Entity;
import dev.theskidster.light.graphics.Light;
import dev.theskidster.light.main.App;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.main.Window;
import dev.theskidster.shadercore.GLProgram;
import java.util.HashMap;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public abstract class Scene {
    
    public static final int MAX_LIGHTS = 32;
    
    private int currLightIndex;
    private int numLights = 1;
    
    public final String name;
    private static Camera camera;
    private final Vector3f noValue = new Vector3f();
    
    public final HashMap<String, Entity> entities = new HashMap<>();
    
    private final LightSource[] lightSources     = new LightSource[MAX_LIGHTS];
    private final LightSource[] lightSourcesCopy = new LightSource[MAX_LIGHTS];
    
    public Scene(String name) {
        this.name = name;
        lightSources[0] = new LightSource(true, Light.daylight());
    }
    
    public abstract void update();
    
    public abstract void render(GLProgram sceneProgram, Camera camera, int shadowMapTexHandle);    

    public abstract void exit();
    
    //TODO: the following methods will be package private in XJGE.
    public void setLightingUniforms(GLProgram sceneProgram, int PCFValue, Matrix4f lightSpace) {
        for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
            if(lightSources[i] != null) {
                if(lightSources[i].getEnabled()) {
                    sceneProgram.setUniform("uLights[" + i + "].brightness", lightSources[i].getBrightness());
                    sceneProgram.setUniform("uLights[" + i + "].contrast",   lightSources[i].getContrast());
                    sceneProgram.setUniform("uLights[" + i + "].distance",   lightSources[i].getDistance());
                    sceneProgram.setUniform("uLights[" + i + "].position",   lightSources[i].getPosition());
                    sceneProgram.setUniform("uLights[" + i + "].ambient",    lightSources[i].getAmbientColor());
                    sceneProgram.setUniform("uLights[" + i + "].diffuse",    lightSources[i].getDiffuseColor());
                    sceneProgram.setUniform("uLights[" + i + "].specular",   lightSources[i].getSpecularColor());
                } else {
                    sceneProgram.setUniform("uLights[" + i + "].brightness", 0f); //f needs to be included otherwise it'll use the int version!
                    sceneProgram.setUniform("uLights[" + i + "].contrast",   0f);
                    sceneProgram.setUniform("uLights[" + i + "].distance",   0f); //TODO: include note that distance does not necessarily align with world coordinates.
                    sceneProgram.setUniform("uLights[" + i + "].position",   noValue);
                    sceneProgram.setUniform("uLights[" + i + "].ambient",    noValue);
                    sceneProgram.setUniform("uLights[" + i + "].diffuse",    noValue);
                    sceneProgram.setUniform("uLights[" + i + "].specular",   noValue);
                }
            }
        }
        
        sceneProgram.setUniform("uNumLights", numLights);
        sceneProgram.setUniform("uPCFValue", PCFValue);
        sceneProgram.setUniform("uLightSpace", false, lightSpace);
        
        App.checkGLError();
        
        /*
        vec3 ambient = light.ambient;

        vec3 lightDir = normalize(light.position - ioFragPos);
        float diff    = max(dot(normal, lightDir), -light.contrast);
        vec3 diffuse  = diff * light.diffuse;

        float linear    = 0.0014f / light.brightness;
        float quadratic = 0.000007f / light.brightness;
        float dist      = length(light.position - ioFragPos);
        float attenuate = 1.0f / (1.0f + linear * dist + quadratic * (dist * dist));

        vec3 cameraDir  = normalize(uCamPos - ioFragPos);
        vec3 reflectDir = reflect(-lightDir, normal);
        float spec      = pow(max(dot(cameraDir, reflectDir), 0), 256);
        vec3 specular   = spec * light.specular;

        ambient *= attenuate;
        diffuse *= attenuate;

        return (ambient + diffuse + specular) * ioColor;
        */
    }
    
    public LightSource[] getLightSources() {
        System.arraycopy(lightSources, 0, lightSourcesCopy, 0, numLights);
        return lightSourcesCopy;
    }
    
    public void updateLightSources() {
        for(LightSource lightSource : lightSources) {
            if(lightSource != null) lightSource.update();
        }
    }
    
    public void renderLightSources(GLProgram sceneProgram, Camera camera) {
        for(LightSource lightSource : lightSources) {
            if(lightSource != null) lightSource.render(sceneProgram, camera.getPosition(), camera.getDirection(), camera.getUp());
        }
    }
    
    public static void setCameraReference(Camera reference) {
        camera = reference;
    }
    
    protected final void setCameraPosition(float x, float y, float z) {
        camera.setPosition(x, y, z);
    }
    
    protected final void setCameraDirection(float yaw, float pitch) {
        camera.setDirection(yaw, pitch, Window.getMouseX(), Window.getMouseY());
    }
    
    private void findNumLights() {
        numLights = 1;
        
        for(LightSource lightSource : lightSources) {
            if(lightSource != null) numLights++;
        }
    }
    
    protected final void addLight(Light light) {
        boolean search = true;
        
        for(int i = 1; search; i++) {
            if(i < MAX_LIGHTS) {
                if(lightSources[i] != null) {
                    if(!lightSources[i].getEnabled()) {
                        lightSources[i] = new LightSource(false, light, lightSources[i]);
                    }
                } else {
                    lightSources[i] = new LightSource(false, light);
                    search = false;
                }
            } else {
                currLightIndex = (currLightIndex == MAX_LIGHTS - 1) ? 1 : currLightIndex + 1;
                lightSources[currLightIndex] = new LightSource(false, light, lightSources[currLightIndex]);
                search = false;
            }
        }
        
        findNumLights();
    }
    
    protected final void addLightAtIndex(int index, Light light) {
        try {
            if(light == null) {
                throw new NullPointerException();
            } else {
                lightSources[index] = new LightSource(index == 0, light, lightSources[index]);
                findNumLights();
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            JLogger.setModule("core");
            JLogger.logWarning("Failed to add light object at index " + index, e);
            JLogger.setModule(null);
        }
    }
    
}