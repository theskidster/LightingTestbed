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
    
    public static Light pointLight = new Light(3f, 1f, 1.25f, new Vector3f(2, 1, 0), Color.RED);
    
    private EntityCube cube = new EntityCube(-12, 3, 0, 5, 5, 5);
    
    public TestScene() {
        super("test");
        
        setCameraPosition(6, 4, 10);
        setCameraDirection(-120, 20);
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.SILVER, 50, 50));
        entities.put("cube", cube);
        entities.put("teapot", new EntityTeapot(10, 2.5f, 0));
        
        addLight(pointLight);
        addLightAtIndex(0, Light.midnight());
    }
    
    @Override
    public void update() {
        cube.angleY += 0.5f;
        cube.angleZ += 0.5f;
        
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