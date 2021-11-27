package dev.theskidster.light.entity;

import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.light.main.App;
import dev.theskidster.shadercore.GLProgram;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
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
            g.vertices.put(-(size / 2)).put(-(size / 2)).put(0)   .put(color.r).put(color.g).put(color.b);
            g.vertices.put(0)          .put(size / 2)   .put(0)   .put(color.r).put(color.g).put(color.b);
            g.vertices.put(size / 2)   .put(-(size / 2)).put(0)   .put(color.r).put(color.g).put(color.b);
            
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