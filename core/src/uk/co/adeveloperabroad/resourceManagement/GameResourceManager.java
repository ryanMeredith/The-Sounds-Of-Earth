package uk.co.adeveloperabroad.resourceManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.uwsoft.editor.renderer.resources.IResourceLoader;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;


/**
 * Created by snow on 28/01/16.
 * class for particular game (to be modified)
 */
public class GameResourceManager extends AsynchronousResourceManager {

    public Texture record = new Texture(Gdx.files.internal("splash/recordLoading.png"));

   public GameResourceManager() {

       loadAnimationAtlasPack("spriteAnimations/walkPacked/walk.atlas");
       loadAnimationAtlasPack("spriteAnimations/headAnimPacked/head.atlas");
       loadAnimationAtlasPack("spriteAnimations/recordPacked/smallRecord.atlas");
       // get all VO data
       initAllSceneData();
       // get image pack
       loadAtlasPack();


    }





}
