package dev.theskidster.light.main;

import dev.theskidster.light.scene.Scene;
import dev.theskidster.shadercore.GLProgram;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
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
    
    int textureWidth  = 1024;
    int textureHeight = 1024;
    
    int PCFValue = 2;
    
    ShadowMap() {
        fbo = glGenFramebuffers();
        
        textureHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, textureWidth, textureHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
    }
    
    public void generate(Scene scene, GLProgram depthProgram, Vector3f camUp) {
        
    }
    
}