package buggyEngine;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author El Juanelo
 */
public class Window {
    
    private int width;
    private int heigth;
    private String title;
    private long glfwWindow;
    
    private float r, g, b, a;
    
    private static Window instance = null;
    
    private Window(){
        this.width = 1920;
        this.heigth = 1080;
        this.title = "Mario";
        this.r = 1f;
        this.g = 1f;
        this.b = 1f;
        this.a = 1f;
    }
    
    public static Window get(){
        if(Window.instance == null)
            Window.instance = new Window();
        
        return Window.instance;
    }
    
    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        
        init();
        loop();
        
        // Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        
        // Terminate GLFW and then free the error callbacks
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    
    public void init(){
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();
        
        // Initialize GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW.");
        }
        
        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        
        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.heigth, this.title, NULL, NULL);
        if(glfwWindow == NULL)
            throw new IllegalStateException("Failed to create the GLFW window.");
        
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        
        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);
        
        // Make the window visible
        glfwShowWindow(glfwWindow);
        
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }
    
    public void loop(){
        while(!glfwWindowShouldClose(glfwWindow)){
            // Poll events
            glfwPollEvents();
            
            glClearColor(r,g,b,a);
            glClear(GL_COLOR_BUFFER_BIT);
            
            
            glfwSwapBuffers(glfwWindow);
        }
    }
}
