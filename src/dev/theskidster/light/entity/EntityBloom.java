package dev.theskidster.light.entity;

import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.light.main.App;
import dev.theskidster.shadercore.GLProgram;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * Nov 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityBloom extends Entity {

    private Graphics g;
    
    public EntityBloom(float x, float y, float z, Color color, float size) {
        super(x, y, z);
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(24);
            
            //(vec3 position), (vec3 color)
            g.vertices.put(-(size / 2)).put(-(size / 2)).put(0)   .put(color.r + 1).put(color.g + 1).put(color.b + 1);
            g.vertices.put(0)          .put(size / 2)   .put(0)   .put(color.r + 1).put(color.g + 1).put(color.b + 1);
            g.vertices.put(size / 2)   .put(-(size / 2)).put(0)   .put(color.r + 1).put(color.g + 1).put(color.b + 1);
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update() {
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram sceneProgram, int shadowMapTexHandle) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(g.vao);
        
        sceneProgram.setUniform("uType", 5);
        sceneProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glDisable(GL_DEPTH_TEST);
        
        App.checkGLError();
    }

    @Override
    public void castShadow(GLProgram depthProgram) {
    }

    
    
}