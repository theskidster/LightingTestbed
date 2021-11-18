package dev.theskidster.dshade.main;

import dev.theskidster.dshade.scene.Scene;
import dev.theskidster.jlogger.JLogger;
import dev.theskidster.shadercore.ShaderCore;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public final class App {

    private static int tickCount = 0;
    
    private static boolean vSync = true;
    
    private final Monitor monitor;
    private final Window window;
    private static Scene scene;
    
    App() {
        if(!glfwInit()) {
            JLogger.logSevere("Failed to initialize GLFW.", null);
        }
        
        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            JLogger.logSevere("Unsupported operating system. Use a 64 bit Windows system.", null);
        } else {
            if(!System.getProperty("os.arch").contains("64")) {
                JLogger.logSevere("Unsupported architecture. Windows system must be 64 bit.", null);
            }
        }
        
        monitor = new Monitor();
        window  = new Window("Deferred Shading", monitor);
        
        glfwMakeContextCurrent(Window.handle);
        GL.createCapabilities();
        
        JLogger.newHorizontalLine();
        JLogger.logInfo("OS NAME:\t\t" + System.getProperty("os.name"));
        JLogger.logInfo("JAVA VERSION:\t" + System.getProperty("java.version"));
        JLogger.logInfo("GLFW VERSION:\t" + glfwGetVersionString());
        JLogger.logInfo("OPENGL VERSION:\t" + glGetString(GL_VERSION));
        JLogger.newHorizontalLine();
        JLogger.newLine();
        
        ShaderCore.setFilepath("/dev/theskidster/dshade/shaders/");
        
        //Create the shader program for the applications heads up display.
        {
            
        }
        
        //Create the shader program for rendering objects within the 3D scene.
        {
            
        }
    }
    
    void start() {
        window.show(monitor);
        
        int cycles = 0;
        int fps = 0;
        final double TARGET_DELTA = 1 / 60.0;
        double prevTime = glfwGetTime();
        double currTime;
        double delta = 0;
        double deltaMetric = 0;
        boolean ticked;
        
        while(!glfwWindowShouldClose(Window.handle)) {
            currTime = glfwGetTime();
            
            delta += currTime - prevTime;
            if(delta < TARGET_DELTA && vSync) delta = TARGET_DELTA;
            
            prevTime = currTime;
            ticked   = false;
            
            while(delta >= TARGET_DELTA) {
                deltaMetric = delta;
                
                delta -= TARGET_DELTA;
                ticked = true;
                tickCount = (tickCount == Integer.MAX_VALUE) ? 0 : tickCount + 1;
                
                glfwPollEvents();
                
                //scene.update();
                
                if(tick(60)) {
                    fps    = cycles;
                    cycles = 0;
                }
            }
            
            glViewport(0, 0, window.getWidth(), window.getHeight());
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            //TODO: render scene
            
            glfwSwapBuffers(Window.handle);
            
            if(!ticked) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {}
            } else {
                cycles++;
            }
        }
        
        GL.destroy();
        glfwTerminate();
    }
    
    public static boolean tick(int cycles) {
        return tickCount % cycles == 0;
    }
    
    public static void setScene(Scene newScene) {
        if(scene != null) scene.exit();
        scene = newScene;
        JLogger.logInfo("Entered scene \"" + scene.name + "\"");
    }
    
}