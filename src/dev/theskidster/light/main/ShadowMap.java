package dev.theskidster.light.main;

import dev.theskidster.light.scene.Scene;
import dev.theskidster.shadercore.GLProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Nov 20, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class ShadowMap {

    private final int fbo;
    final int textureHandle;
    
    int textureWidth  = 2048;
    int textureHeight = 2048;
    
    float nearPlane = 1f;
    float farPlane  = 100f;
    
    int PCFValue = 0;
    
    private final Vector3f lightDir  = new Vector3f();
    private final Matrix4f lightView = new Matrix4f();
    private final Matrix4f lightProj = new Matrix4f();
    
    final Matrix4f lightSpace = new Matrix4f();
    
    ShadowMap() {
        fbo = glGenFramebuffers();
        
        textureHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, textureWidth, textureHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        
        //float[] borderColor = new float[] {1, 1, 1, 1};
        //glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureHandle, 0);
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
    public void generate(Scene scene, GLProgram depthProgram, Vector3f camUp) {
        lightProj.setOrtho(-100f, 100f, -100f, 100f, nearPlane, farPlane);
        lightView.setLookAt(scene.getLightSources()[0].getPosition(), lightDir, camUp);
        lightProj.mul(lightView, lightSpace);
        
        depthProgram.use();
        depthProgram.setUniform("uLightSpace", false, lightSpace);
        
        glViewport(0, 0, textureWidth, textureHeight);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glClear(GL_DEPTH_BUFFER_BIT);
            glBindTexture(GL_TEXTURE_2D, textureHandle);
            
            scene.entities.values().forEach(entity -> {
                entity.castShadow(depthProgram);
            });
            
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    
}