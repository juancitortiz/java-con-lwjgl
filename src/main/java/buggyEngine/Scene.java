package buggyEngine;

/**
 *
 * @author El Juanelo
 */
public abstract class Scene {
    
    protected Camera camera;
    
    public Scene(){}
    
    public void init(){
        
    }
    
    public abstract void update(float deltaTime);
}
