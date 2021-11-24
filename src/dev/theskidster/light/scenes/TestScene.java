package dev.theskidster.light.scenes;

import dev.theskidster.light.entity.EntityCube;
import dev.theskidster.light.entity.EntityPlane;
import dev.theskidster.light.entity.EntityTeapot;
import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.graphics.Light;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.scene.Scene;
import dev.theskidster.shadercore.GLProgram;
import org.joml.Vector3f;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {
    
    private EntityCube cube = new EntityCube(-3, 0, 0, 1, 2, 1);
    
    public TestScene() {
        super("test");
        
        setCameraPosition(6, 4, 10);
        setCameraDirection(-120, 20);
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.SILVER, 50, 50));
        entities.put("cube", cube);
        entities.put("teapot", new EntityTeapot(10, 0, 0));
        
        cube.color = Color.BLUE;
        
        addLight(new Light(0.005f, 0, new Vector3f(2, 0, 0), Color.YELLOW, Color.YELLOW, Color.WHITE));
        addLightAtIndex(0, Light.midnight());
    }
    
    @Override
    public void update() {
        cube.angleY += 1f;
        cube.angleZ += 1f;
        
        entities.values().forEach(entity -> entity.update());
    }

    @Override
    public void render(GLProgram sceneProgram, Camera camera, int shadowMapTexHandle) {
        entities.values().forEach(entity -> entity.render(sceneProgram, shadowMapTexHandle));
    }

    @Override
    public void exit() {
    }

}