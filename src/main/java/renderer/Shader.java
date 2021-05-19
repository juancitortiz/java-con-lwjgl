package renderer;

import java.io.IOException;
import java.nio.file.*;
import static org.lwjgl.opengl.GL20.*;
import org.joml.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 *
 * @author El Juanelo
 */
public class Shader {
    
    private int shaderProgramID;
    private boolean beingUsed = false;
    
    private String vertexSrc;
    private String fragmentSrc;
    private String filePath;
    
    public Shader(String filePath){
        this.filePath = filePath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
            
            // Find first pattern after #type pattern
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();
            
            // Find second pattern after #type pattern
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();
            
            if(firstPattern.equals("vertex")){
                vertexSrc = splitString[1];
            } else if(firstPattern.equals("fragment")){
                fragmentSrc = splitString[1];
            }
            else
                throw new IOException("Unexpected token '" + firstPattern + "'");
            
            if(secondPattern.equals("vertex")){
                vertexSrc = splitString[2];
            } else if(secondPattern.equals("fragment")){
                fragmentSrc = splitString[2];
            }
            else
                throw new IOException("Unexpected token '" + secondPattern + "'");
        } catch(IOException ex){
            ex.printStackTrace();
            assert false : "Error: could not open file for shader: '" + filePath + "'";
        }
    }
    
    public void compile(){
        /**
         * Compile and link shaders
         */
        
        int vertexID, fragmentID;
        
        // First load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);
        
        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+ this.filePath +"'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }
        
        // First load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);
        
        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+ this.filePath +"'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }
        
        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        
        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+ this.filePath +"'\n\tLinking shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }
    
    public void use(){
        if(!beingUsed){
            // Bind shader Program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    
    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }
    
    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    
    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }
    
    public void uploadVec4f(String varName, Vector4f vec4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec4.x, vec4.y, vec4.z, vec4.w);
    }
    
    public void uploadVec3f(String varName, Vector3f vec3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec3.x, vec3.y, vec3.z);
    }
    
    public void uploadVec2f(String varName, Vector2f vec2){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec2.x, vec2.y);
    }
    
    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }
    
    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
}
