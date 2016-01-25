package uk.co.adeveloperabroad.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import uk.co.adeveloperabroad.MessageType;
import uk.co.adeveloperabroad.RegionComparator;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;

/**
 * Created by snow on 17/01/16.
 */
public class RecordLabelController implements IScript , Telegraph {

    private Array<TextureAtlas.AtlasRegion> labelAtlasRegions;
    private Animation labelAnimation;
    private static float LABEL_FPS = 1.0f / 15.0f;
    private float labelAnimationTime;

    private RecordSpeedComponent recordSpeedComponent;
    private TextureRegionComponent textureRegionComponent;

    @Override
    public void init(Entity entity) {

        TextureAtlas labelAtlas = new TextureAtlas(Gdx.files.internal("spriteAnimations/recordPacked/record.atlas"));
        labelAtlasRegions = new Array<TextureAtlas.AtlasRegion>(labelAtlas.getRegions());
        labelAtlasRegions.sort(new RegionComparator());
        labelAnimation = new Animation(LABEL_FPS, labelAtlasRegions, Animation.PlayMode.LOOP_REVERSED);

        textureRegionComponent = entity.getComponent(TextureRegionComponent.class);
        recordSpeedComponent = entity.getComponent(RecordSpeedComponent.class);
    }

    @Override
    public void act(float delta) {
        labelAnimationTime += recordSpeedComponent.recordSpeed * delta * 0.5f;
        textureRegionComponent.region = labelAnimation.getKeyFrame(labelAnimationTime);
    }

    @Override
    public void dispose() {

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
