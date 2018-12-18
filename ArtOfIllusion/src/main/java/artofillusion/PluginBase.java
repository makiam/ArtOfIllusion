/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artofillusion;

/**
 *
 * @author maksim.khramov
 */
public class PluginBase implements Plugin {

    @Override
    public void processMessage(int message, Object... args) {
        switch(message) {
            case APPLICATION_STARTING: {  onApplicationStarting(args); break; }
            case APPLICATION_STOPPING: {  onApplicationStopping(args); break; }
            case SCENE_WINDOW_CREATED: {  onSceneWindowCreated(args); break; }
            case SCENE_SAVED : {  onSceneSaved(args); break; }
        }
    }
    
    public void onApplicationStarting(Object... args) {
        
    }
    
    public void onApplicationStopping(Object... args) {
        
    }
    
    public void onSceneWindowCreated(Object... args) {
        
    }

    public void onSceneSaved(Object... args) {
        
    }
}
