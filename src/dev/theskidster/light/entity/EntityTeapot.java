package dev.theskidster.light.entity;

import dev.theskidster.light.graphics.Model;
import dev.theskidster.shadercore.GLProgram;

/**
 * Nov 22, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class EntityTeapot extends Entity {

    private Model model;
    
    public EntityTeapot(float x, float y, float z) {
        super(x, y, z);
        
        model = new Model("mod_teapot.fbx");
    }

    @Override
    public void update() {
        model.delocalizeNormal();
        model.meshes.forEach(mesh -> {
            mesh.modelMatrix.translation(position);
            mesh.modelMatrix.rotateX((float) Math.toRadians(-135));
            mesh.modelMatrix.rotateY((float) Math.toRadians(90));
            mesh.modelMatrix.scale(0.15f);
        });
    }

    @Override
    public void render(GLProgram sceneProgram, int shadowMapTexHandle) {
        model.render(sceneProgram, shadowMapTexHandle);
    }

    @Override
    public void castShadow(GLProgram depthProgram) {
        model.castShadow(depthProgram);
    }

}
