package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import uk.co.adeveloperabroad.MessageType;
import uk.co.adeveloperabroad.RegionComparator;

/**
 * Created by snow on 17/01/16.
 */
public class AlienController implements IScript, Telegraph{

    private Array<TextureAtlas.AtlasRegion> alienAtlasRegions;
    private Animation alienAnimation;
    private float animationTimeAlien;
    private static float ALIEN_FPS = 1.0f / 180.0f;
    private static Integer ALIEN_FRAMES_PER_LEG = 15;
    private Integer nextLeg = 1;
    private boolean hasTrackFinished = false;

    private TextureRegionComponent textureRegionComponent;


    @Override
    public void init(Entity entity) {

        textureRegionComponent = entity.getComponent(TextureRegionComponent.class);

        TextureAtlas alienAtlas = new TextureAtlas(Gdx.files.internal("spriteAnimations/alien/alien.atlas"));
        alienAtlasRegions = new Array<TextureAtlas.AtlasRegion>(alienAtlas.getRegions());
        alienAtlasRegions.sort(new RegionComparator());
        alienAnimation = new Animation(ALIEN_FPS, alienAtlasRegions, Animation.PlayMode.LOOP);
        addListeners();
    }


    @Override
    public void act(float delta) {

        animationTimeAlien = MathUtils.clamp(
                animationTimeAlien + Gdx.graphics.getDeltaTime(),
                0.0f,
                (nextLeg - 1) * ALIEN_FPS * ALIEN_FRAMES_PER_LEG);

        textureRegionComponent.region = alienAnimation.getKeyFrame(animationTimeAlien);
    }

    private void moveLeg(int leg) {

        if (!hasTrackFinished) {
            if (leg == 1) {
                nextLeg = 2;
            }

            if (leg == 2) {
                nextLeg = 3;
            }

            if (leg == 3) {
                nextLeg = 1;
                MessageManager.getInstance().dispatchMessage(0, this, MessageType.moreSpeed);

            }
            animationTimeAlien = ALIEN_FPS * ALIEN_FRAMES_PER_LEG * (leg - 1);
        }

    }

    @Override
    public void dispose() {

    }

    private void addListeners() {
        MessageManager.getInstance().addListener(this, MessageType.timeout);
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
        MessageManager.getInstance().addListener(this, MessageType.leg1);
        MessageManager.getInstance().addListener(this, MessageType.leg2);
        MessageManager.getInstance().addListener(this, MessageType.leg3);
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
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
            case MessageType.leg1:
                moveLeg(1);
                break;

            case MessageType.leg2:
                moveLeg(2);
                break;

            case MessageType.leg3:
                moveLeg(3);
                break;
        }
        return true;
    }
}


