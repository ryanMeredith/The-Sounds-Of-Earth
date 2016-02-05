package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.NodeComponent;
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

    private SpeechController speechController;
    private Sound alienTalk;

    public AlienHeadController(TextureAtlas alienAtlas, Sound alienTalk) {
        this.alienAtlas = alienAtlas;
        this.alienTalk = alienTalk;
    }

    @Override
    public void init(Entity entity) {

        NodeComponent nodeComponent = entity.getComponent(NodeComponent.class);
        Entity head = nodeComponent.children.get(0);
        Entity speech = nodeComponent.children.get(1);
        speechController = new SpeechController(speech, alienTalk);

        textureRegionComponent = head.getComponent(TextureRegionComponent.class);

        alienAtlasRegions = new Array<TextureAtlas.AtlasRegion>(alienAtlas.getRegions());
        alienAtlasRegions.sort(new RegionComparator());
        alienAnimation = new Animation(ALIEN_FPS, alienAtlasRegions, Animation.PlayMode.NORMAL);
        addListeners();
    }


    @Override
    public void act(float delta) {

        if (runAnimation) {
            alienAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            animationTimeAlien  = animationTimeAlien + Gdx.graphics.getDeltaTime();
            textureRegionComponent.region = alienAnimation.getKeyFrame(animationTimeAlien);
        }

        if (alienAnimation.isAnimationFinished(animationTimeAlien) && !hasTrackFinished) {
            runAnimation = false;
            animationTimeAlien = 0;
        }

        speechController.act(delta);

        if (speechController.isTalking) {
            alienAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG );
            animationTimeAlien  = animationTimeAlien + Gdx.graphics.getDeltaTime();
            textureRegionComponent.region = alienAnimation.getKeyFrame(animationTimeAlien);
        }

    }



    @Override
    public void dispose() {
        alienAtlas.dispose();

        if (speechController != null) {
            speechController.dispose();
        }
        alienTalk.dispose();
    }

    private void addListeners() {
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
        MessageManager.getInstance().addListener(this, MessageType.timeout);
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
        MessageManager.getInstance().addListener(this, MessageType.leg1);
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        switch (msg.message) {

            case MessageType.timeout:
                hasTrackFinished = true;
                break;
            case MessageType.startingPositions:
                animationTimeAlien = 0;
                textureRegionComponent.region = alienAnimation.getKeyFrame(animationTimeAlien);
                hasTrackFinished = false;
                break;
            case MessageType.win:
                hasTrackFinished = true;
                break;
            case MessageType.lose:
                hasTrackFinished = true;
                break;
            case MessageType.leg1:
                runAnimation = true;
                animationTimeAlien = 0;
                break;
        }
        return true;
    }
}


