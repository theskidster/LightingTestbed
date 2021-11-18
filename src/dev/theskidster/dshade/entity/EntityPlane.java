package dev.theskidster.dshade.entity;

import dev.theskidster.dshade.graphics.Color;
import dev.theskidster.dshade.graphics.Graphics;
import dev.theskidster.dshade.main.App;
import dev.theskidster.shadercore.GLProgram;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityPlane extends Entity {

    private Graphics g;
    private Color color;
    
    public EntityPlane(float x, float y, float z, Color color, float width, float depth) {
        super(x, y, z);
        this.color = color;
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(12);
            g.indices  = stack.mallocInt(6);
            
            //(vec position)
            g.vertices.put(-width).put(0).put(-depth);
            g.vertices.put(-width).put(0) .put(depth);
            g.vertices .put(width).put(0) .put(depth);
            g.vertices. put(width).put(0).put(-depth);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }

    @Override
    public void update() {
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram sceneProgram) {
        glBindVertexArray(g.vao);
        
        sceneProgram.setUniform("uColor", color.asVec3());
        sceneProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        App.checkGLError();
    }

}
