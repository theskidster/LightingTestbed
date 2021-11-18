package dev.theskidster.dshade.scene;

import dev.theskidster.dshade.entity.Entity;
import dev.theskidster.dshade.main.Camera;
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
    
    protected Camera camera;
    
    protected final HashMap<String, Entity> entities = new HashMap<>();
    
    public Scene(String name) {
        this.name = name;
    }
    
    public abstract void update();
    
    public abstract void render(GLProgram sceneProgram);    

    public abstract void exit();
    
    public void setCameraReference(Camera camera) {
        this.camera = camera;
    }
    
}