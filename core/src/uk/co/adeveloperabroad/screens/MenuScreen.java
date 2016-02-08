package uk.co.adeveloperabroad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.controllers.MenuButtonController;
import uk.co.adeveloperabroad.controllers.VoyagerController;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;

/**
 * Created by snow on 03/02/16.
 */
public class MenuScreen implements Screen {

    private SceneLoader sceneLoader;
    private Viewport viewport;
    private Music bach;
    private Float volume = 0f;


    public MenuScreen (Viewport viewport, GameResourceManager rm){
        sceneLoader = new SceneLoader(rm);
        this.viewport = viewport;

        sceneLoader.loadScene("menu", viewport);

        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.getChild("voyager").addScript(new VoyagerController());
        root.getChild("intro").addScript(new MenuButtonController());
        root.getChild("play").addScript(new MenuButtonController());
        root.getChild("about").addScript(new MenuButtonController());
        bach = rm.assetManager.get("music/bachFree.ogg", Music.class);
        bach.setLooping(true);
    }

    @Override
    public void show() {
        volume = 0f;
        bach.setVolume(volume);
        bach.play();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        sceneLoader.getEngine().update(delta);


        if (volume < 0.8) {
            volume += delta * 0.05f;
            bach.setVolume(volume);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        bach.pause();
    }

    @Override
    public void dispose() {
        bach.dispose();
    }
}
