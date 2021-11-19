package dev.theskidster.light.tech;

import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.light.graphics.Texture;
import org.joml.Vector2f;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
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
    private static Texture texture;
    
    static {
        
    }
    
    LightSource(boolean isWorldLight, Light light) {
        this.isWorldLight = isWorldLight;
        this.light        = light;
        
        float offset = (isWorldLight) ? 0 : 0.5f;
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-0.5f) .put(0.5f).put(0) .put(0)     .put(0);
            g.vertices .put(0.5f) .put(0.5f).put(0) .put(offset).put(0);
            g.vertices .put(0.5f).put(-0.5f).put(0) .put(offset).put(1);
            g.vertices.put(-0.5f).put(-0.5f).put(0) .put(0)     .put(1);
            
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
    
    void update() {
        g.modelMatrix.translation(light.position);
    }
    
}