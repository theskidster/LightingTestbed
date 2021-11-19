package dev.theskidster.light.main;

import dev.theskidster.shadercore.GLProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public final class Camera {

    private float pitch;
    private float yaw = -90f;
    
    double prevX;
    double prevY;
    
    final Vector3f position  = new Vector3f();
    final Vector3f direction = new Vector3f(0, 0, -1);
    final Vector3f up        = new Vector3f(0, 1, 0);
    
    private final Vector3f tempVec1 = new Vector3f();
    private final Vector3f tempVec2 = new Vector3f();
    
    private final Matrix4f view = new Matrix4f();
    private final Matrix4f proj = new Matrix4f();
    
    void updateViewport(int width, int height) {
        proj.setPerspective((float) Math.toRadians(45f), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
    void render(GLProgram sceneProgram) {
        view.setLookAt(position, position.add(direction, tempVec1), up);
        
        sceneProgram.setUniform("uView", false, view);
        sceneProgram.setUniform("uProjection", false, proj);
    }
    
    private float getChangeIntensity(double currValue, double prevValue, float sensitivity) {
        return (float) (currValue - prevValue) * sensitivity;
    }
    
    void setPosition(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            float speedX = getChangeIntensity(-xPos, -prevX, 0.017f);
            float speedY = getChangeIntensity(-yPos, -prevY, 0.017f);
            
            position.add(direction.cross(up, tempVec1).normalize().mul(speedX));
            
            tempVec1.set(
                    (float) (Math.cos(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch))), 
                    0, 
                    (float) (Math.sin(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch))));
            
            position.add(0, direction.cross(tempVec1, tempVec2).normalize().mul(speedY).y, 0);
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
    void setDirection(double xPos, double yPos) {
        if(xPos != prevX || yPos != prevY) {
            yaw   += getChangeIntensity(xPos, prevX, 0.22f);
            pitch += getChangeIntensity(yPos, prevY, 0.22f);
            
            if(pitch > 89f)  pitch = 89f;
            if(pitch < -89f) pitch = -89f;
            
            direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
            direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            
            prevX = xPos;
            prevY = yPos;
        }
    }
    
    void dolly(float speed) {
        position.add(direction.mul(speed, tempVec1));
    }
    
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }
    
    public void setDirection(float yaw, float pitch, double xPos, double yPos) {
        if(yaw > 180f)  yaw = 180f;
        if(yaw < -180f) yaw = -180f;
        
        this.yaw = yaw;
        
        if(pitch > 89f)  pitch = 89f;
        if(pitch < -89f) pitch = -89f;
        
        this.pitch = pitch;

        direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        direction.y = (float) Math.sin(Math.toRadians(pitch)) * -1;
        direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        
        prevX = xPos;
        prevY = yPos;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public Vector3f getDirection() {
        return direction;
    }
    
    public Vector3f getUp() {
        return up;
    }
    
}