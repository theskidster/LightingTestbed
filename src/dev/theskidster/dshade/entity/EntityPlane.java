package dev.theskidster.dshade.entity;

import dev.theskidster.dshade.graphics.Graphics;
import dev.theskidster.shadercore.GLProgram;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityPlane extends Entity {

    private Graphics g;
    
    public EntityPlane(float x, float y, float z) {
        super(x, y, z);
        
        g = new Graphics();
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GLProgram sceneProgram) {
    }

}
