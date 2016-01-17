package uk.co.adeveloperabroad;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;

public class SoundsOfEarth extends ApplicationAdapter {

    private Viewport viewport;
    private SceneLoader sceneLoader;
    private MainStage mainStage;

    @Override
    public void create() {


        viewport = new FitViewport(160, 96);
        sceneLoader = new SceneLoader();

        mainStage = new MainStage(viewport, sceneLoader);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(mainStage);
        //inputMultiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainStage.act();
        mainStage.draw();
    }

    @Override
    public void dispose() {

    }


}


