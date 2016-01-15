package uk.co.adeveloperabroad;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;
import java.util.Comparator;

import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.components.WalkBoxComponent;
import uk.co.adeveloperabroad.systems.PictureSystem;
import uk.co.adeveloperabroad.systems.WalkBoxSystem;

public class SoundsOfEarth extends ApplicationAdapter implements Telegraph {

    private Viewport viewport;
    private SceneLoader sceneLoader;

    private LevelManager levelManager;

    private float recordSpeed;
    private static float RECORD_DRAGSPEED = 0.8f;
    private static float LEG_IMPULSE_SPEED = 0.5f;
    private Sound mysterySound;
    private Long soundId;

    private Array<TextureAtlas.AtlasRegion> labelAtlasRegions;
    private Animation labelAnimation;
    private float animationTimeLabel;
    private static float LABEL_FPS = 1.0f / 15.0f;

    private Array<TextureAtlas.AtlasRegion> alienAtlasRegions;
    private Animation alienAnimation;
    private float animationTimeAlien;
    private static float ALIEN_FPS = 1.0f / 180.0f;
    private static Integer ALIEN_FRAMES_PER_LEG = 15;
    private Integer nextLeg = 1;

    private SpriteBatch batch;

    private PictureSystem pictureSystem = new PictureSystem();
    private WalkBoxSystem walkBoxSystem = new WalkBoxSystem();
    private boolean hasGuessed = false;
    private int score = 0;


    @Override
    public void create() {

        batch = new SpriteBatch();
        viewport = new FitViewport(160, 96);
        sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MainScene", viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());

        Entity scoreEntitiy = root.getChild("score").getEntity();

 
        LabelComponent labelComponent = ComponentRetriever.get(scoreEntitiy, LabelComponent.class);
        labelComponent.setText("hello");

        ComponentRetriever.addMapper(PictureComponent.class);
        ComponentRetriever.addMapper(WalkBoxComponent.class);

        sceneLoader.addComponentsByTagName("input", WalkBoxComponent.class);
        sceneLoader.getEngine().addSystem(walkBoxSystem);
        sceneLoader.getEngine().addSystem(pictureSystem);


        addMessageListeners();

        Json json = new Json();
        Array<Level> levels = json.fromJson(Array.class, Level.class, Gdx.files.internal("levels/levelResources"));
        levelManager = new LevelManager(levels, root);

        TextureAtlas labelAtlas = new TextureAtlas(Gdx.files.internal("spriteAnimations/label/label.atlas"));
        labelAtlasRegions = new Array<TextureAtlas.AtlasRegion>(labelAtlas.getRegions());
        labelAtlasRegions.sort(new RegionComparator());
        labelAnimation = new Animation(LABEL_FPS, labelAtlasRegions, Animation.PlayMode.LOOP);

        TextureAtlas alienAtlas = new TextureAtlas(Gdx.files.internal("spriteAnimations/alien/alien.atlas"));
        alienAtlasRegions = new Array<TextureAtlas.AtlasRegion>(alienAtlas.getRegions());
        alienAtlasRegions.sort(new RegionComparator());
        alienAnimation = new Animation(ALIEN_FPS, alienAtlasRegions, Animation.PlayMode.LOOP);

        playLevel(1);
    }



    protected void playLevel(int levelNumber) {
        sceneLoader.addComponentsByTagName("picture", PictureComponent.class);
        levelManager.loadLevel(levelNumber);
        loadMysterySound();
        startPositions();
    }

    @Override
    public void render() {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());

        recordSpeed -= Gdx.graphics.getDeltaTime() * RECORD_DRAGSPEED;
        recordSpeed = MathUtils.clamp(recordSpeed, 0, 10);

        if (recordSpeed == 0) {
            mysterySound.pause(soundId);
        } else {
            playSound(recordSpeed);
        }

        animationTimeLabel += Gdx.graphics.getDeltaTime() * recordSpeed;
        animationTimeAlien = MathUtils.clamp(
                animationTimeAlien + Gdx.graphics.getDeltaTime(),
                0.0f,
                (nextLeg - 1) * ALIEN_FPS * ALIEN_FRAMES_PER_LEG);

        batch.begin();
        batch.draw(labelAnimation.getKeyFrame(animationTimeLabel), 130.0f, 118.0f);
        batch.draw(alienAnimation.getKeyFrame(animationTimeAlien), 100.0f, 195.0f);
        batch.end();

    }

    @Override
    public void dispose() {
        mysterySound.dispose();
    }

    private void playSound(float speed) {
        Gdx.app.log("pitch", Float.toString(speed * 0.2f));
        mysterySound.setPitch(soundId, speed * 0.2f);
        mysterySound.resume(soundId);
    }

    private void moveLeg(int leg) {

        if (!hasGuessed) {
            if (leg == 1) {
                nextLeg = 2;
            }

            if (leg == 2) {
                nextLeg = 3;
            }

            if (leg == 3) {
                nextLeg = 1;
                recordSpeed += LEG_IMPULSE_SPEED;
            }
            animationTimeAlien = ALIEN_FPS * ALIEN_FRAMES_PER_LEG * (leg - 1);
        }

    }

    protected void startPositions() {
        animationTimeAlien = 0;
        animationTimeLabel = 0;
        recordSpeed = 0;
        sceneLoader.getEngine().getSystem(WalkBoxSystem.class).nextLeg = 1;
        hasGuessed = false;
    }

    protected void loadMysterySound() {
        mysterySound = levelManager.mysterySound;
        soundId = mysterySound.loop();
        mysterySound.pause(soundId);
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        if (msg.message == MessageType.win) {
            score ++;
           guessed();
        }

        if (msg.message == MessageType.lose) {
            guessed();
        }


        if (msg.message == MessageType.leg1) {
            moveLeg(1);
        }

        if (msg.message == MessageType.leg2) {
            moveLeg(2);
        }
        if (msg.message == MessageType.leg3) {
            moveLeg(3);
        }
        return true;
    }

    public void guessed() {

        hasGuessed = true;

        ImmutableArray<Entity> pictureEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(PictureComponent.class).get());
        for (Entity pictureEntity : pictureEntities) {
            pictureEntity.remove(PictureComponent.class);
        }

        if (levelManager.finalLevelNumber >= levelManager.currentLevel.levelNumber + 1) {
            playLevel(levelManager.currentLevel.levelNumber + 1);
        } else {
            System.out.println("game over");
        }
    }


    private void addMessageListeners() {
        MessageManager.getInstance().addListener(this, MessageType.leg1);
        MessageManager.getInstance().addListener(this, MessageType.leg2);
        MessageManager.getInstance().addListener(this, MessageType.leg3);
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
    }


    private static class RegionComparator implements Comparator<TextureAtlas.AtlasRegion> {
        @Override
        public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
            return region1.name.compareTo(region2.name);
        }
    }
}


