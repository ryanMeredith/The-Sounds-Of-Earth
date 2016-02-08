package uk.co.adeveloperabroad.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;

import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;

/**
 * Created by snow on 08/02/16.
 */
public class IntroductionScreen implements Screen {

    private Viewport viewport;
    private SceneLoader sceneLoader;

    public IntroductionScreen (Viewport viewport, GameResourceManager rm) {
        this.viewport = viewport;
        sceneLoader = new SceneLoader(rm);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
