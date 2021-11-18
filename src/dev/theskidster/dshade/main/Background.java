package dev.theskidster.dshade.main;

import static dev.theskidster.dshade.graphics.Color.BLACK;
import dev.theskidster.dshade.graphics.Graphics;
import dev.theskidster.shadercore.GLProgram;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
final class Background {

    private final Graphics g;
    
    Background(float x, float y, float width, float height) {
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(8);
            g.indices  = stack.mallocInt(6);
            
            //(vec position)
            g.vertices.put(x)        .put(y + height);
            g.vertices.put(x + width).put(y + height);
            g.vertices.put(x + width).put(y);
            g.vertices.put(x)        .put(y);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 2, GL_FLOAT, false, (2 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }
    
    void render(GLProgram hudProgram) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(g.vao);
        
        hudProgram.setUniform("uType", 1);
        hudProgram.setUniform("uColor", BLACK.asVec3());
        hudProgram.setUniform("uOpacity", 0.5f);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        
        App.checkGLError();
    }
    
}
