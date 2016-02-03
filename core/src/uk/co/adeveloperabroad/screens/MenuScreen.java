package uk.co.adeveloperabroad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

    SceneLoader sceneLoader;
    Viewport viewport;


    public MenuScreen(Viewport viewport, SceneLoader sceneLoader){
        this.sceneLoader = sceneLoader;
        this.viewport = viewport;
    }

    @Override
    public void show() {
        sceneLoader.loadScene("menu", viewport);
        GameResourceManager rm = (GameResourceManager) sceneLoader.getRm();
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.getChild("voyager").addScript(new VoyagerController());
        root.getChild("intro").addScript(new MenuButtonController());
        root.getChild("play").addScript(new MenuButtonController());
        root.getChild("about").addScript(new MenuButtonController());
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
                : 0));

        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
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

    }

    @Override
    public void dispose() {

    }
}
