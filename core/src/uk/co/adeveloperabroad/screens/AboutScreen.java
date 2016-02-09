package uk.co.adeveloperabroad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.controllers.HomeButtonController;
import uk.co.adeveloperabroad.controllers.LinkController;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;
import uk.co.adeveloperabroad.utility.MessageType;

/**
 * Created by snow on 05/02/16.
 */
public class AboutScreen implements Screen {

    private Viewport viewport;
    private SceneLoader sceneLoader;

    public AboutScreen(Viewport viewport, GameResourceManager rm) {
        sceneLoader = new SceneLoader(rm);
        this.viewport = viewport;
        sceneLoader.loadScene("about", viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        root.getChild("homeButton").addScript(new HomeButtonController());
        root.getChild("wikiLink").addScript(new LinkController());
        root.getChild("recordLink").addScript(new LinkController());

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
                : 0));

        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            MessageManager.getInstance().dispatchMessage(0, null, MessageType.goToMenu);
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

    }

    @Override
    public void dispose() {

    }


}
