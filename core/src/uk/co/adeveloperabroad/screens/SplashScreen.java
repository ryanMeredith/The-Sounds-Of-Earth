package uk.co.adeveloperabroad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;


/**
 * Created by snow on 01/02/16.
 */
public class SplashScreen implements Screen {


    private Batch batch;

    private GameResourceManager rm;
    private Float fadeSpeed = 0.05f;
    private Float fadeTime = 0.0f;
    public Float alpha = 0.0f;

    public Sprite record;

    public Boolean fadeIn = true;
    public Boolean fadeOut = false;


    public SplashScreen(GameResourceManager rm, Batch batch) {
        this.rm = rm;
        this.batch = batch;
        rm.record.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        record = new Sprite(rm.record);
        record.setPosition((Gdx.graphics.getWidth() * 0.5f - record.getWidth() * 0.5f) -9 ,135);

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

    }

    public void fadeOut(){
        fadeIn = false;
        fadeOut = true;
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
