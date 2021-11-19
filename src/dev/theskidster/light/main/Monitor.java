package dev.theskidster.light.main;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
final class Monitor {

    final int width;
    final int height;
    final int refreshRate;
    
    final long handle;
    
    final String info;
    
    private final GLFWVidMode videoMode;
    
    Monitor() {        
        handle    = glfwGetPrimaryMonitor();
        videoMode = glfwGetVideoMode(handle);
        
        width       = videoMode.width();
        height      = videoMode.height();
        refreshRate = videoMode.refreshRate();
        
        info = width + "x" + height + " " + refreshRate + "hz";
    }
    
}
