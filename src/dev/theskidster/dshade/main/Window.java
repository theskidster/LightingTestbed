package dev.theskidster.dshade.main;

import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public final class Window {

    private final int initialPosX;
    private final int initialPosY;
    private int width  = 1280;
    private int height = 720;
    
    private static float mousePosX;
    private static float mousePosY;
    
    static long handle;
    
    private boolean mouseMiddleHeld;
    private boolean mouseRightHeld;
    
    Window(String title, Monitor monitor) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xStartBuf = stack.mallocInt(1);
            IntBuffer yStartBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xStartBuf, yStartBuf);
            
            initialPosX = Math.round((monitor.width - width) / 2) + xStartBuf.get();
            initialPosY = Math.round((monitor.height - height) / 2) + yStartBuf.get();
        }
        
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
    }
    
    void show(Monitor monitor, Camera camera) {
        glfwSetWindowMonitor(handle, NULL, initialPosX, initialPosY, width, height, monitor.refreshRate);
        glfwSetWindowPos(handle, initialPosX, initialPosY);
        glfwSwapInterval(1);
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwShowWindow(handle);
        
        camera.updateViewport(width, height);
        
        glfwSetCursorPosCallback(handle, (window, x, y) -> {
            if(mouseMiddleHeld ^ mouseRightHeld) {
                if(mouseMiddleHeld) camera.setPosition(x, y);
                if(mouseRightHeld)  camera.setDirection(x, y);
            } else {
                camera.prevX = x;
                camera.prevY = y;
            }
            
            mousePosX = (float) x;
            mousePosY = (float) y;
        });
        
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            switch(button) {
                case GLFW_MOUSE_BUTTON_MIDDLE -> mouseMiddleHeld = (action == GLFW_PRESS);
                case GLFW_MOUSE_BUTTON_RIGHT  -> mouseRightHeld = (action == GLFW_PRESS);
            }
        });
        
        glfwSetScrollCallback(handle, (window, xOffset, yOffset) -> {
            camera.dolly((float) yOffset);
        });
        
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            if(key == GLFW_KEY_ESCAPE) glfwSetWindowShouldClose(handle, true);
        });
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public static float getMouseX() {
        return mousePosX;
    }
    
    public static float getMouseY() {
        return mousePosY;
    }
    
}