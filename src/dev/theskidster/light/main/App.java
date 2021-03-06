package dev.theskidster.light.main;

import dev.theskidster.light.graphics.Color;
import dev.theskidster.light.scene.Scene;
import dev.theskidster.light.scenes.TestScene;
import dev.theskidster.jlogger.JLogger;
import dev.theskidster.shadercore.BufferType;
import dev.theskidster.shadercore.GLProgram;
import dev.theskidster.shadercore.Shader;
import dev.theskidster.shadercore.ShaderCore;
import java.util.LinkedList;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL30.*;

/**
 * Nov 17, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public final class App {

    private static int tickCount = 0;
    private final int fbo;
    
    private final int[] fbos = new int[2];
    private final int[] textures = new int[2];
    
    private static boolean vSync = true;
    
    public static final String ASSETS_PATH = "/dev/theskidster/light/assets/";
    
    private final Monitor monitor;
    private final Window window;
    private final Camera camera;
    private final GLProgram hudProgram;
    private final GLProgram sceneProgram;
    private final GLProgram depthProgram;
    private final GLProgram blurProgram;
    private final Font font;
    private final Background background;
    private final ShadowMap shadowMap;
    private final FrameBufferTexture viewport;
    private final FrameBufferTexture bloomTex;
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
        window  = new Window("Lighting Testbed", monitor);
        
        glfwMakeContextCurrent(Window.handle);
        GL.createCapabilities();
        
        JLogger.newHorizontalLine();
        JLogger.logInfo("OS NAME:\t\t" + System.getProperty("os.name"));
        JLogger.logInfo("JAVA VERSION:\t" + System.getProperty("java.version"));
        JLogger.logInfo("GLFW VERSION:\t" + glfwGetVersionString());
        JLogger.logInfo("OPENGL VERSION:\t" + glGetString(GL_VERSION));
        JLogger.newHorizontalLine();
        JLogger.newLine();
        
        ShaderCore.setFilepath("/dev/theskidster/light/shaders/");
        
        //Create the shader program for the applications heads up display.
        {
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("hudVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("hudFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            hudProgram = new GLProgram(shaderSourceFiles, "hud");
            
            hudProgram.use();
            hudProgram.addUniform(BufferType.INT,   "uType");
            hudProgram.addUniform(BufferType.FLOAT, "uOpacity");
            hudProgram.addUniform(BufferType.VEC3,  "uColor");
            hudProgram.addUniform(BufferType.MAT4,  "uProjection");
        }
        
        //Create the shader program for rendering objects within the 3D scene.
        {
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("sceneVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("sceneFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            sceneProgram = new GLProgram(shaderSourceFiles, "scene");
            
            sceneProgram.use();
            sceneProgram.addUniform(BufferType.INT,  "uType");
            sceneProgram.addUniform(BufferType.INT,  "uNumLights");
            sceneProgram.addUniform(BufferType.INT,  "uTexture");
            sceneProgram.addUniform(BufferType.INT,  "uShadowMap");
            sceneProgram.addUniform(BufferType.INT,  "uBloomTexture");
            sceneProgram.addUniform(BufferType.INT,  "uPCFValue");
            sceneProgram.addUniform(BufferType.INT,  "uShine");
            sceneProgram.addUniform(BufferType.VEC2, "uTexCoords");
            sceneProgram.addUniform(BufferType.VEC3, "uColor");
            sceneProgram.addUniform(BufferType.VEC3, "uCamPos");
            sceneProgram.addUniform(BufferType.MAT3, "uNormal");
            sceneProgram.addUniform(BufferType.MAT4, "uModel");
            sceneProgram.addUniform(BufferType.MAT4, "uView");
            sceneProgram.addUniform(BufferType.MAT4, "uProjection");
            sceneProgram.addUniform(BufferType.MAT4, "uLightSpace");
            
            for(int i = 0; i < Scene.MAX_LIGHTS; i++) {
                sceneProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].brightness");
                sceneProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].contrast");
                sceneProgram.addUniform(BufferType.FLOAT, "uLights[" + i + "].distance");
                sceneProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].position");
                sceneProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].ambient");
                sceneProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].diffuse");
                sceneProgram.addUniform(BufferType.VEC3,  "uLights[" + i + "].specular");
            }
        }
        
        //Create shader program that will generate shadow map output.
        {
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("depthVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("depthFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            depthProgram = new GLProgram(shaderSourceFiles, "depth");
            
            depthProgram.use();
            depthProgram.addUniform(BufferType.INT, "uTexture");
            depthProgram.addUniform(BufferType.MAT4, "uModel");
            depthProgram.addUniform(BufferType.MAT4, "uLightSpace");
        }
        
        //Create shader program for applying gaussian blur.
        {
            var shaderSourceFiles = new LinkedList<Shader>() {{
                add(new Shader("blurVertex.glsl", GL_VERTEX_SHADER));
                add(new Shader("blurFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            blurProgram = new GLProgram(shaderSourceFiles, "blur");
            
            blurProgram.use();
            blurProgram.addUniform(BufferType.INT,   "uBloomTexture");
            blurProgram.addUniform(BufferType.INT,   "uHorizontal");
            blurProgram.addUniform(BufferType.FLOAT, "uWeight");
            blurProgram.addUniform(BufferType.MAT4,  "uProjection");
        }
        
        camera       = new Camera();
        font         = new Font("fnt_debug_mono.ttf", 12);
        background   = new Background(0, window.getHeight() - 130, 300, 130);
        shadowMap    = new ShadowMap();
        viewport     = new FrameBufferTexture(window.getWidth(), window.getHeight());
        bloomTex     = new FrameBufferTexture(window.getWidth(), window.getHeight());
        
        Scene.setCameraReference(camera);
        
        { //Establish framebuffer texture.
            fbo = glGenFramebuffers();
            
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewport.texHandle, 0);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, bloomTex.texHandle, 0);
            
            int rbo = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, rbo);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, window.getWidth(), window.getHeight());
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
            
            int[] attachments = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1};
            glDrawBuffers(attachments);
            
            checkFBStatus(GL_FRAMEBUFFER);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
        
        glGenFramebuffers(fbos);
        glGenTextures(textures);
        
        for(int i = 0; i < 2; i++) {
            glBindFramebuffer(GL_FRAMEBUFFER, fbos[i]);
            glBindTexture(GL_TEXTURE_2D, textures[i]);
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, window.getWidth(), window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textures[i], 0);
            
            checkFBStatus(GL_FRAMEBUFFER);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
    }
    
    void start() {
        scene = new TestScene();
        window.show(monitor, camera);
        
        int cycles = 0;
        int fps = 0;
        final double TARGET_DELTA = 1 / 60.0;
        double prevTime = glfwGetTime();
        double currTime;
        double delta = 0;
        double deltaMetric = 0;
        boolean ticked;
        
        Matrix4f projMatrix = new Matrix4f();
        projMatrix.setOrtho(window.getWidth(), 0, 0, window.getHeight(), 0, 1);
        
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
                
                scene.update();
                scene.updateLightSources();
                
                if(tick(60)) {
                    fps    = cycles;
                    cycles = 0;
                }
            }
            
            shadowMap.generate(scene, depthProgram, camera.up);
            
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glViewport(0, 0, window.getWidth(), window.getHeight());
            //glClearColor(1, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            //Render Scene.
            {
                sceneProgram.use();
                
                sceneProgram.setUniform("uCamPos", camera.position);
                scene.setLightingUniforms(sceneProgram, shadowMap.PCFValue, shadowMap.lightSpace);
                
                camera.render(sceneProgram);
                scene.render(sceneProgram, camera, shadowMap.textureHandle);
                scene.renderLightSources(sceneProgram, camera);
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            
            blurProgram.use();
            blurProgram.setUniform("uProjection", false, projMatrix);
            
            boolean firstPass  = true;
            boolean horizontal = true;
            int blurWeight = 10;
            
            for(int i = 0; i < blurWeight; i++) {
                int value     = (horizontal) ? 1 : 0;
                int invValue  = (horizontal) ? 0 : 1;
                int texHandle = bloomTex.texHandle;
                
                glBindFramebuffer(GL_FRAMEBUFFER, fbos[invValue]);
                bloomTex.render(blurProgram, (firstPass) ? texHandle : textures[value], horizontal);
                
                horizontal = !horizontal;
                if(firstPass) firstPass = false;
            }
            
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            //Render HUD.
            {
                hudProgram.use();
                font.projMatrix.setOrtho(0, window.getWidth(), 0, window.getHeight(), 0, Integer.MAX_VALUE);
                hudProgram.setUniform("uProjection", false, font.projMatrix);
                
                background.render(hudProgram);
                font.drawString("FPS: " + fps, 12, window.getHeight() - 20, Color.WHITE, hudProgram);
                font.drawString("DELTA: " + (float) deltaMetric, 12, window.getHeight() - 40, Color.WHITE, hudProgram);
                font.drawString("TICKED: " + ticked, 12, window.getHeight() - 60, Color.WHITE, hudProgram);
                font.drawString("VSYNC: " + vSync, 12, window.getHeight() - 80, Color.YELLOW, hudProgram);
                font.drawString("MONITOR: " + monitor.info, 12, window.getHeight() - 100, Color.YELLOW, hudProgram);
                font.drawString("MEM FREE: " + Runtime.getRuntime().freeMemory(), 12, window.getHeight() - 120, Color.CYAN, hudProgram);
            }
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            
            sceneProgram.use();
            sceneProgram.setUniform("uProjection", false, projMatrix);
            viewport.render(sceneProgram, viewport.texHandle, textures[1]);
            
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
    
    public static void checkGLError() {
        int glError = glGetError();
        
        if(glError != GL_NO_ERROR) {
            String desc = "";
            
            switch(glError) {
                case GL_INVALID_ENUM      -> desc = "invalid enum";
                case GL_INVALID_VALUE     -> desc = "invalid value";
                case GL_INVALID_OPERATION -> desc = "invalid operation";
                case GL_STACK_OVERFLOW    -> desc = "stack overflow";
                case GL_STACK_UNDERFLOW   -> desc = "stack underflow";
                case GL_OUT_OF_MEMORY     -> desc = "out of memory";
            }
            
            JLogger.logSevere("OpenGL Error: (" + glError + ") " + desc, null);
        }
    }
    
    void checkFBStatus(int target) {
        int status  = glCheckFramebufferStatus(target);
        String desc = "";
        
        if(status != GL_FRAMEBUFFER_COMPLETE) {
            switch(status) {
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT         -> desc = "incomplete attachment";
                case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> desc = "missing attachment";
                case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER        -> desc = "incomplete draw buffer";
                case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER        -> desc = "incomplete read buffer";
                case GL_FRAMEBUFFER_UNSUPPORTED                   -> desc = "unsupported";
                case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE        -> desc = "incomplete multisample";
                case GL_FRAMEBUFFER_UNDEFINED                     -> desc = "undefined";
            }
            
            JLogger.setModule("core");
            JLogger.logSevere("Framebuffer Error: (" + status + ") " + desc, null);
        }
    }
    
}