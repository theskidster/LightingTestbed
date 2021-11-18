package dev.theskidster.dshade.scene;

import dev.theskidster.dshade.entity.EntityCube;
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
        
        setCameraPosition(6, 4, 10);
        setCameraDirection(-120, 20);
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.GRAY, 25, 25));
        entities.put("cube", new EntityCube(0, 0, 0, 1, 1, 1));
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