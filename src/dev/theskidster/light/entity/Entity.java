package dev.theskidster.light.entity;

import dev.theskidster.shadercore.GLProgram;
import org.joml.Vector3f;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public abstract class Entity {
    
    public Vector3f position;
    
    public Entity(float x, float y, float z) {
        position = new Vector3f(x, y, z);
    }
    
    public abstract void update();
    
    public abstract void render(GLProgram sceneProgram);
    
}