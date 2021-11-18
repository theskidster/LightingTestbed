package dev.theskidster.dshade.scene;

import dev.theskidster.dshade.entity.EntityPlane;
import dev.theskidster.dshade.graphics.Color;
import dev.theskidster.shadercore.GLProgram;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    public TestScene() {
        super("test");
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.WHITE, 10, 10));
    }

    @Override
    public void update() {
        entities.values().forEach(entity -> entity.update());
    }

    @Override
    public void render(GLProgram sceneProgram) {
        entities.values().forEach(entity -> entity.render(sceneProgram));
    }

    @Override
    public void exit() {
    }

}