package uk.co.adeveloperabroad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import uk.co.adeveloperabroad.adMob.AdvertDisplay;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;
import uk.co.adeveloperabroad.screens.AboutScreen;
import uk.co.adeveloperabroad.screens.GameScreen;
import uk.co.adeveloperabroad.screens.IntroductionScreen;
import uk.co.adeveloperabroad.screens.MenuScreen;
import uk.co.adeveloperabroad.screens.SplashScreen;
import uk.co.adeveloperabroad.utility.MessageType;
import uk.co.adeveloperabroad.utility.ScreenNumber;

public class SoundsOfEarth extends Game implements Telegraph{

    private Viewport viewport;
    private GameResourceManager rm;


    private Screen currentScreen;
    private Screen menuScreen;
    private Screen aboutScreen;
    private Screen gameScreen;
    private Screen introductionScreen;

    private Boolean isLoaded = false;

    private Boolean changeScreen = false;
    private int screenNumber;

    private AdvertDisplay advertDisplay;

    public SoundsOfEarth(AdvertDisplay advertDisplay){
        this.advertDisplay = advertDisplay;
    }

    @Override
    public void create() {

        if (advertDisplay != null) {
            advertDisplay.showAdvert();
        }

        viewport = new FitViewport(160, 96);
        rm = new GameResourceManager();

        currentScreen = new SplashScreen(rm);
        setScreen(currentScreen);
        addMessageListeners();
    }

    @Override
    public void render() {
        super.render();

        rm.update();

        if (!isLoaded && !rm.isCurrentlyLoading) {
            isLoaded = true;
            createScreens();
        }



        if (changeScreen && screenNumber != 0) {

            currentScreen = null;
            changeScreen = false;
            switch (screenNumber) {
                case ScreenNumber.AboutScreen :
                    currentScreen = aboutScreen;
                    break;
                case ScreenNumber.GameScreen:
                    currentScreen = gameScreen;
                    break;
                case ScreenNumber.IntroductionScreen :
                    currentScreen = introductionScreen;
                    break;
                case ScreenNumber.MenuScreen:
                    currentScreen = menuScreen;
                    break;
            }
            setScreen(currentScreen);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        rm.dispose();

    }

    @Override
    public boolean handleMessage(Telegram msg) {

        // the set screen needs to come in the game loop or Overlap gets upset :(
        switch (msg.message) {

            case MessageType.goToGame:
                Gdx.app.log("setScreen", "gotoGame");
                screenNumber = ScreenNumber.GameScreen;
                changeScreen = true;
                break;

            case MessageType.goToAbout:
                Gdx.app.log("setScreen", "goToAbout");
                screenNumber = ScreenNumber.AboutScreen;
                changeScreen = true;
                break;

            case MessageType.goToIntroduction:
                Gdx.app.log("setScreen", "goToIntroduction");
                screenNumber = ScreenNumber.IntroductionScreen;
                changeScreen = true;
                break;

            case MessageType.goToMenu:
                Gdx.app.log("setScreen", "goToMenu");
                screenNumber = ScreenNumber.MenuScreen;
                changeScreen = true;
                break;
        }

        return true;
    }

    private void addMessageListeners() {
//                MessageManager.getInstance().setDebugEnabled(true);
        MessageManager.getInstance().addListener(this, MessageType.goToGame);
        MessageManager.getInstance().addListener(this, MessageType.goToAbout);
        MessageManager.getInstance().addListener(this, MessageType.goToMenu);
        MessageManager.getInstance().addListener(this, MessageType.goToIntroduction);
    }

    private void createScreens() {
        aboutScreen = new AboutScreen(viewport, rm);
        gameScreen = new GameScreen(viewport, rm);
        introductionScreen = new IntroductionScreen(viewport, rm);
        menuScreen = new MenuScreen(viewport, rm);
    }
}


