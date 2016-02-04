package uk.co.adeveloperabroad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;

import uk.co.adeveloperabroad.levels.Level;
import uk.co.adeveloperabroad.levels.LevelManager;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;
import uk.co.adeveloperabroad.screens.GameScreen;
import uk.co.adeveloperabroad.screens.MenuScreen;
import uk.co.adeveloperabroad.screens.SplashScreen;
import uk.co.adeveloperabroad.utility.MessageType;

public class SoundsOfEarth extends Game implements Telegraph{

    private Viewport viewport;
    private SceneLoader sceneLoader;
    private GameResourceManager rm;
    private SplashScreen splashScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private LevelManager levelManager;
    private Boolean isLoaded = false;

    @Override
    public void create() {
        viewport = new FitViewport(160, 96);


        rm = new GameResourceManager();
        sceneLoader = new SceneLoader(rm);

        Json json = new Json();
        Array<Level> levels = json.fromJson(Array.class, Level.class, Gdx.files.internal("levels/levelResources"));
        levelManager = new LevelManager(levels, rm);
        // load level one sound
        levelManager.preLoadSound(1);

        splashScreen = new SplashScreen(rm, sceneLoader.getBatch());
        setScreen(splashScreen);

        MessageManager.getInstance().setDebugEnabled(true);
        MessageManager.getInstance().addListener(this, MessageType.playGame);

    }

    @Override
    public void render() {
        super.render();
        rm.update();
        MessageManager.getInstance().update(Gdx.graphics.getDeltaTime());
//        // only show splash at start of game;
        if (splashScreen != null) {
            transitionSplash();
        }

    }

    private void transitionSplash() {
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
//            setScreen(menuScreen);
            setScreen(gameScreen);
        }
    }

    private void instantiateScreens() {
//        menuScreen = new MenuScreen(viewport, sceneLoader);
        gameScreen = new GameScreen(viewport, sceneLoader, levelManager);
    }

    @Override
    public void dispose() {
//        rm.dispose();
//        gameScreen.dispose();
//        levelManager.dispose();
    }


    @Override
    public boolean handleMessage(Telegram msg) {

        if (msg.message == MessageType.playGame) {
            gameScreen = new GameScreen(viewport, sceneLoader, levelManager);
            setScreen(gameScreen);
            menuScreen.dispose();
        }


        return true;
    }
}


