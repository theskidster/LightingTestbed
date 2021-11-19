package dev.theskidster.light.scene;

import dev.theskidster.light.entity.EntityCube;
import dev.theskidster.light.entity.EntityPlane;
import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.tech.Light;
import dev.theskidster.light.tech.LightSource;
import dev.theskidster.shadercore.GLProgram;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {

    private LightSource lightSource;
    
    public TestScene() {
        super("test");
        
        setCameraPosition(6, 4, 10);
        setCameraDirection(-120, 20);
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.GRAY, 25, 25));
        entities.put("cube", new EntityCube(0, 0, 0, 1, 1, 1));
        
        lightSource = new LightSource(true, Light.daylight());
    }

    @Override
    public void update() {
        entities.values().forEach(entity -> entity.update());
        
        lightSource.update();
    }

    @Override
    public void render(GLProgram sceneProgram, Camera camera) {
        entities.values().forEach(entity -> entity.render(sceneProgram));
        
        lightSource.render(sceneProgram, camera.getPosition(), camera.getDirection(), camera.getUp());
    }

    @Override
    public void exit() {
    }

}