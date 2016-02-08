package uk.co.adeveloperabroad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.uwsoft.editor.renderer.SceneLoader;

import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;
import uk.co.adeveloperabroad.utility.MessageType;


/**
 * Created by snow on 01/02/16.
 */
public class SplashScreen implements Screen {


    private Batch batch;

    private GameResourceManager rm;
    private Float fadeSpeed = 0.05f;
    private Float fadeTime = 0.0f;
    private Float alpha = 0.0f;

    private Sprite record;

    private Boolean fadeIn = true;
    private Boolean fadeOut = false;
    private Boolean isLoaded = false;

    public SplashScreen(GameResourceManager rm) {

        this.rm = rm;
        batch = new SpriteBatch();
        rm.record.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        record = new Sprite(rm.record, rm.record.getWidth(), rm.record.getHeight());
        record.setPosition(
                (Gdx.graphics.getWidth() * 0.5f - record.getWidth() * 0.5f) ,
                (Gdx.graphics.getHeight() * 0.5f - record.getHeight() * 0.5f) + 10);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

        fadeTime += delta;
        batch.begin();
        record.setAlpha(alpha);
        record.setRotation(-360 * rm.percentageLoaded);
        record.draw(batch);
        batch.end();


        if (fadeIn) {
            if (fadeTime >= fadeSpeed && alpha != 1) {
                alpha = MathUtils.clamp(alpha + 0.025f, 0, 1);
                fadeTime = 0.0f;
            }
        }


        if (fadeOut) {
            if (fadeTime >= fadeSpeed && alpha != 0) {
                alpha = MathUtils.clamp(alpha - 0.025f, 0, 1);
                fadeTime = 0.0f;
            }
        }
        transitionSplash();

    }

    public void fadeOut(){
        fadeIn = false;
        fadeOut = true;
    }

    private void transitionSplash() {
        // if fully loaded and faded in, create screen objects and fade out
        if (!rm.isCurrentlyLoading  & alpha == 1) {
            fadeOut();
        }

        // when fully faded out start the game
        if (fadeOut && alpha == 0 && !isLoaded) {
            isLoaded = true;
            Gdx.app.log("messageSent", "goToMenu ");
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
        record.getTexture().dispose();
        rm.record.dispose();
    }
}
