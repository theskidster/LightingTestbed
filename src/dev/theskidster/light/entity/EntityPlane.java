package dev.theskidster.light.entity;

import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.graphics.Graphics;
import dev.theskidster.light.main.App;
import dev.theskidster.shadercore.GLProgram;
import org.joml.Matrix3f;
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
    private Matrix3f normal = new Matrix3f();
    
    public EntityPlane(float x, float y, float z, Color color, float width, float depth) {
        super(x, y, z);
        this.color = color;
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(24);
            g.indices  = stack.mallocInt(6);
            
            //(vec position), (vec2 normal)
            g.vertices.put(-width).put(0).put(-depth)   .put(0).put(1).put(0);
            g.vertices.put(-width).put(0) .put(depth)   .put(0).put(1).put(0);
            g.vertices .put(width).put(0) .put(depth)   .put(0).put(1).put(0);
            g.vertices. put(width).put(0).put(-depth)   .put(0).put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(3);
    }

    @Override
    public void update() {
        normal.set(g.modelMatrix.invert());
        g.modelMatrix.translation(position);
    }

    @Override
    public void render(GLProgram sceneProgram) {
        glBindVertexArray(g.vao);
        
        sceneProgram.setUniform("uType", 0);
        sceneProgram.setUniform("uColor", color.asVec3());
        sceneProgram.setUniform("uModel", false, g.modelMatrix);
        sceneProgram.setUniform("uNormal", true, normal);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        App.checkGLError();
    }

    @Override
    public void castShadow(GLProgram depthProgram) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(g.vao);
        
        depthProgram.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        
        App.checkGLError();
    }

}
