package dev.theskidster.light.scenes;

import dev.theskidster.light.entity.EntityCube;
import dev.theskidster.light.entity.EntityPlane;
import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.graphics.Model;
import dev.theskidster.light.scene.Scene;
import dev.theskidster.shadercore.GLProgram;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TestScene extends Scene {
    
    private EntityCube cube = new EntityCube(0, 0, 0, 1, 1, 1);
    private Model model;
    
    public TestScene() {
        super("test");
        
        setCameraPosition(6, 4, 10);
        setCameraDirection(-120, 20);
        
        entities.put("plane", new EntityPlane(0, -2, 0, Color.GRAY, 50, 50));
        entities.put("cube", cube);
        
        model = new Model("mod_teapot.fbx");
        
        //addLight(new Light(0.5f, 1f, new Vector3f(2, 0, 0), Color.LIME, Color.LIME));
        
        //addLightAtIndex(0, Light.midnight());
    }

    @Override
    public void update() {
        cube.angleY += 1f;
        
        entities.values().forEach(entity -> entity.update());
    }

    @Override
    public void render(GLProgram sceneProgram, Camera camera) {
        entities.values().forEach(entity -> entity.render(sceneProgram));
        
        model.render(sceneProgram);
    }

    @Override
    public void exit() {
    }

}