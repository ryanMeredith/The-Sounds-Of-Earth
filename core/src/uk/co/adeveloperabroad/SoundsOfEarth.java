package uk.co.adeveloperabroad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;

import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;
import uk.co.adeveloperabroad.screens.GameScreen;
import uk.co.adeveloperabroad.screens.SplashScreen;

public class SoundsOfEarth extends Game {

    private Viewport viewport;
    private SceneLoader sceneLoader;
    private GameResourceManager rm;
    private SplashScreen splashScreen;
    private GameScreen gameScreen;

    private Boolean isLoaded = false;

    @Override
    public void create() {
        viewport = new FitViewport(160, 96);
        rm = new GameResourceManager();
        sceneLoader = new SceneLoader(rm);
        splashScreen = new SplashScreen(rm, sceneLoader.getBatch());
        setScreen(splashScreen);
    }

    @Override
    public void render() {
        super.render();
        rm.update();

//        // only show splash at start of game;
        if (splashScreen != null) {
            transitionSplash();
        }

    }

    private void transitionSplash() {
        Gdx.app.log("splash alpha", splashScreen.alpha.toString());
        // if fully loaded and faded in, create screen objects and fade out
        if (!rm.isCurrentlyLoading  & splashScreen.alpha == 1) {
            instantiateScreens();
            splashScreen.fadeOut();
        }

        // when fully faded out start the game
        if (splashScreen.fadeOut && splashScreen.alpha == 0 && !isLoaded) {
            splashScreen.dispose();
            splashScreen = null;
            isLoaded = true;
            setScreen(gameScreen);
        }
    }

    private void instantiateScreens() {
        gameScreen = new GameScreen(viewport, sceneLoader);
    }

    @Override
    public void dispose() {
        rm.dispose();
        gameScreen.dispose();
    }


}


