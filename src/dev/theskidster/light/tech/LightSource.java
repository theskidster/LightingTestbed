package dev.theskidster.light.tech;

import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.light.graphics.Texture;
import dev.theskidster.light.main.App;
import dev.theskidster.shadercore.GLProgram;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * Nov 18, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public final class LightSource {

    private final boolean isWorldLight;
    
    private final Light light;
    private final Graphics g;
    private final Vector2f texCoords;
    private static final Texture texture;
    
    static {
        texture = new Texture("spr_light_icons.png");
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    public LightSource(boolean isWorldLight, Light light) {
        this.isWorldLight = isWorldLight;
        this.light        = light;
        
        g         = new Graphics();
        texCoords = new Vector2f((isWorldLight) ? 0 : 0.5f, 0);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-0.5f) .put(0.5f).put(0) .put(0)   .put(0);
            g.vertices .put(0.5f) .put(0.5f).put(0) .put(0.5f).put(0);
            g.vertices .put(0.5f).put(-0.5f).put(0) .put(0.5f).put(1);
            g.vertices.put(-0.5f).put(-0.5f).put(0) .put(0)   .put(1);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    public void update() {
        g.modelMatrix.translation(light.position);
    }
    
    public void render(GLProgram sceneProgram, Vector3f camPos, Vector3f camDir, Vector3f camUp) {
        g.modelMatrix.billboardSpherical(light.position, camPos, camUp);
        g.modelMatrix.scale(camPos.distance(light.position) / 10);
        
        sceneProgram.use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        sceneProgram.setUniform("uType", 2);
        sceneProgram.setUniform("uModel", false, g.modelMatrix);
        sceneProgram.setUniform("uColor", light.ambientColor.asVec3());
        sceneProgram.setUniform("uTexCoords", texCoords);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        
        App.checkGLError();
    }
    
}