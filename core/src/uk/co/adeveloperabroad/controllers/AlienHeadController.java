package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import uk.co.adeveloperabroad.utility.MessageType;
import uk.co.adeveloperabroad.utility.RegionComparator;

/**
 * Created by snow on 17/01/16.
 */
public class AlienHeadController implements IScript, Telegraph{

    private Array<TextureAtlas.AtlasRegion> alienAtlasRegions;
    private Animation alienAnimation;
    private float animationTimeAlien;
    private static float ALIEN_FPS = 1.0f / 24.0f;
    private boolean runAnimation = false;
    private boolean hasTrackFinished = false;
    private TextureRegionComponent textureRegionComponent;
    private TextureAtlas alienAtlas;


    public AlienHeadController(TextureAtlas alienAtlas) {
        this.alienAtlas = alienAtlas;
    }

    @Override
    public void init(Entity entity) {

        textureRegionComponent = entity.getComponent(TextureRegionComponent.class);

        alienAtlasRegions = new Array<TextureAtlas.AtlasRegion>(alienAtlas.getRegions());
        alienAtlasRegions.sort(new RegionComparator());
        alienAnimation = new Animation(ALIEN_FPS, alienAtlasRegions, Animation.PlayMode.NORMAL);
        addListeners();
    }


    @Override
    public void act(float delta) {

        if (runAnimation ) {
            animationTimeAlien  = animationTimeAlien + Gdx.graphics.getDeltaTime();
            textureRegionComponent.region = alienAnimation.getKeyFrame(animationTimeAlien);
        }

        if (alienAnimation.isAnimationFinished(animationTimeAlien) && !hasTrackFinished) {
            runAnimation = false;
            animationTimeAlien = 0;
        }

    }



    @Override
    public void dispose() {
        alienAtlas.dispose();
    }

    private void addListeners() {
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
        MessageManager.getInstance().addListener(this, MessageType.timeout);
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
        MessageManager.getInstance().addListener(this, MessageType.leg2);
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        switch (msg.message) {

            case MessageType.timeout:
                hasTrackFinished = true;
                break;
            case MessageType.startingPositions:
                animationTimeAlien = 0;
                hasTrackFinished = false;
                break;
            case MessageType.win:
                hasTrackFinished = true;
                break;
            case MessageType.lose:
                hasTrackFinished = true;
                break;
            case MessageType.leg2:
                runAnimation = true;
                break;
        }
        return true;
    }
}


