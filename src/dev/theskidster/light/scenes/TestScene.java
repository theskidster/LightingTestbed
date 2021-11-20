package dev.theskidster.light.scenes;

import dev.theskidster.light.entity.EntityCube;
import dev.theskidster.light.entity.EntityPlane;
import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.graphics.Light;
import dev.theskidster.light.scene.LightSource;
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
    
    private EntityCube cube = new EntityCube(0, 0, 0, 1, 1, 1);
    
    public TestScene() {
        super("test");
        
        setCameraPosition(6, 4, 10);
        setCameraDirection(-120, 20);
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.GRAY, 25, 25));
        entities.put("cube", cube);
        
        addLight(new Light(0.5f, 0.5f, new Vector3f(2, 0, 0), Color.RED, Color.RED));
    }

    @Override
    public void update() {
        cube.angleY += 1f;
        
        entities.values().forEach(entity -> entity.update());
    }

    @Override
    public void render(GLProgram sceneProgram, Camera camera) {
        entities.values().forEach(entity -> entity.render(sceneProgram));
    }

    @Override
    public void exit() {
    }

}