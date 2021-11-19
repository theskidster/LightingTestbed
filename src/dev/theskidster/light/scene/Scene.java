package dev.theskidster.light.scene;

import dev.theskidster.light.entity.Entity;
import dev.theskidster.light.main.Camera;
import dev.theskidster.light.main.Window;
import dev.theskidster.shadercore.GLProgram;
import java.util.HashMap;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public abstract class Scene {
    
    public final String name;
    
    private static Camera camera;
    
    protected final HashMap<String, Entity> entities = new HashMap<>();
    
    public Scene(String name) {
        this.name = name;
    }
    
    public abstract void update();
    
    public abstract void render(GLProgram sceneProgram);    

    public abstract void exit();
    
    public static void setCameraReference(Camera reference) {
        camera = reference;
    }
    
    final void setCameraPosition(float x, float y, float z) {
        camera.setPosition(x, y, z);
    }
    
    final void setCameraDirection(float yaw, float pitch) {
        camera.setDirection(yaw, pitch, Window.getMouseX(), Window.getMouseY());
    }
    
}