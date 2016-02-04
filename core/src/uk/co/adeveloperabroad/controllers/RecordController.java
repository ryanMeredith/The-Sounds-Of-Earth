package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import uk.co.adeveloperabroad.utility.MessageType;
import uk.co.adeveloperabroad.utility.RegionComparator;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;

/**
 * Created by snow on 17/01/16.
 */
public class RecordController implements IScript , Telegraph {

    private Array<TextureAtlas.AtlasRegion> recordAtlasRegions;
    private Animation labelAnimation;
    private static float LABEL_FPS = 1.0f / 8.0f;
    private float labelAnimationTime = 0;

    private RecordSpeedComponent recordSpeedComponent;
    private TextureRegionComponent textureRegionComponent;

    private TextureAtlas recordAtlas;

    public RecordController(TextureAtlas record){
        this.recordAtlas = record;
    }

    @Override
    public void init(Entity entity) {

        recordAtlasRegions = new Array<TextureAtlas.AtlasRegion>(recordAtlas.getRegions());
        recordAtlasRegions.sort(new RegionComparator());
        labelAnimation = new Animation(LABEL_FPS, recordAtlasRegions, Animation.PlayMode.LOOP_REVERSED);

        textureRegionComponent = entity.getComponent(TextureRegionComponent.class);
        recordSpeedComponent = entity.getComponent(RecordSpeedComponent.class);
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
    }

    @Override
    public void act(float delta) {
        labelAnimationTime += recordSpeedComponent.recordSpeed * delta * 0.5f;
        textureRegionComponent.region = labelAnimation.getKeyFrame(labelAnimationTime);
    }

    @Override
    public void dispose() {
        recordAtlas.dispose();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {
            case MessageType.startingPositions:
                labelAnimationTime = 0;
                break;
        }
        return true;
    }
}
