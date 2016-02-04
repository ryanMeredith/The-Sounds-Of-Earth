package uk.co.adeveloperabroad.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;
import uk.co.adeveloperabroad.utility.BinaryDisplay;
import uk.co.adeveloperabroad.levels.LevelManager;
import uk.co.adeveloperabroad.utility.MessageType;
import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;
import uk.co.adeveloperabroad.components.WalkBoxComponent;
import uk.co.adeveloperabroad.controllers.AlienController;
import uk.co.adeveloperabroad.controllers.AlienHeadController;
import uk.co.adeveloperabroad.controllers.PlayAgainController;
import uk.co.adeveloperabroad.controllers.RecordController;
import uk.co.adeveloperabroad.controllers.SpeechController;
import uk.co.adeveloperabroad.controllers.SpeedIndicatorController;
import uk.co.adeveloperabroad.controllers.StylusController;
import uk.co.adeveloperabroad.systems.PictureSystem;
import uk.co.adeveloperabroad.systems.WalkBoxSystem;

/**
 * Created by snow on 17/01/16.
 */
public class GameScreen implements Screen, Telegraph {


    private SceneLoader sceneLoader;
    private Viewport viewport;
    private LevelManager levelManager;


    private BinaryDisplay binaryDisplay = new BinaryDisplay();
    private LabelComponent scoreLabel;
    private LabelComponent trackLabel;
    private int score = 0;

    private float recordSpeed;
    private static float RECORD_DRAGSPEED = 0.8f;
    private static float LEG_IMPULSE_SPEED = 1.2f;

    private Entity stylus;
    private Entity speedIndicator;
    private Entity recordLabel;

    private Long soundId;

    private PictureSystem pictureSystem = new PictureSystem();
    private WalkBoxSystem walkBoxSystem = new WalkBoxSystem();

    public GameScreen(Viewport viewport, SceneLoader sceneLoader, LevelManager levelManager) {
        this.viewport = viewport;
        this.sceneLoader = sceneLoader;
        this.levelManager = levelManager;
    }

    @Override
    public void show() {
        sceneLoader.loadScene("MainScene", viewport);
        GameResourceManager rm = (GameResourceManager) sceneLoader.getRm();
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());
        levelManager.setRoot(root);

        Entity scoreEntity = root.getChild("score").getEntity();
        scoreLabel = ComponentRetriever.get(scoreEntity, LabelComponent.class);
        scoreLabel.setText(binaryDisplay.getScore(score));

        Entity trackEntity = root.getChild("track").getEntity();
        trackLabel = ComponentRetriever.get(trackEntity, LabelComponent.class);

        ComponentRetriever.addMapper(PictureComponent.class);
        ComponentRetriever.addMapper(WalkBoxComponent.class);
        ComponentRetriever.addMapper(RecordSpeedComponent.class);

        sceneLoader.addComponentsByTagName("input", WalkBoxComponent.class);
        sceneLoader.getEngine().addSystem(walkBoxSystem);
        sceneLoader.getEngine().addSystem(pictureSystem);


        root.getChild("alien").addScript(new AlienController(
                rm.assetManager.get("spriteAnimations/walkPacked/walk.atlas", TextureAtlas.class)));

        root.getChild("alienHead").addScript(new AlienHeadController(
                rm.assetManager.get("spriteAnimations/headAnimPacked/head.atlas",
                        TextureAtlas.class)));

        recordLabel = root.getChild("record").getEntity().add(new RecordSpeedComponent());
        root.getChild("record").addScript(new RecordController(
                rm.assetManager.get("spriteAnimations/recordPacked/record.atlas",
                        TextureAtlas.class)));

        root.getChild("speech").addScript(new SpeechController());
        root.getChild("playAgain").addScript(new PlayAgainController());

        stylus = root.getChild("stylus").getEntity().add(new RecordSpeedComponent());
        root.getChild("stylus").addScript(new StylusController());

        speedIndicator = root.getChild("speedIndicator").getEntity().add(new RecordSpeedComponent());
        root.getChild("speedIndicator").addScript(new SpeedIndicatorController());

        addMessageListeners();

        playLevel(1);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
                : 0));

        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());

        recordSpeed -= Gdx.graphics.getDeltaTime() * RECORD_DRAGSPEED;
        recordSpeed = MathUtils.clamp(recordSpeed, 0, 10);
        stylus.getComponent(RecordSpeedComponent.class).recordSpeed = recordSpeed;
        speedIndicator.getComponent(RecordSpeedComponent.class).recordSpeed = recordSpeed;
        recordLabel.getComponent(RecordSpeedComponent.class).recordSpeed = recordSpeed;

        if (recordSpeed == 0) {
            levelManager.mysterySound.pause(soundId);
        } else {
            playSound(recordSpeed);
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }


    protected void playLevel(int trackNumber) {
        sceneLoader.addComponentsByTagName("picture", PictureComponent.class);
        levelManager.loadLevel(trackNumber);
        trackLabel.setText(binaryDisplay.getTrack(trackNumber, levelManager.finalLevelNumber));
        loadMysterySound();
        startPositions();
    }


    private void playSound(float speed) {
        levelManager.mysterySound.pause(soundId);
        levelManager.mysterySound.setPitch(soundId, speed * 0.2f);
        levelManager.mysterySound.setVolume(soundId, 1.0f);
        levelManager.mysterySound.resume(soundId);
    }


    protected void startPositions() {
        recordSpeed = 0;
        MessageManager.getInstance().dispatchMessage(0.0f, this, MessageType.startingPositions);
    }

    protected void loadMysterySound() {
        soundId = levelManager.mysterySound.loop();
        levelManager. mysterySound.pause(soundId);
    }


    public void guessed() {

        lockButtons();
        ImmutableArray<Entity> pictureEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(PictureComponent.class).get());
        for (Entity pictureEntity : pictureEntities) {
            pictureEntity.remove(PictureComponent.class);
        }


        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {

                if (levelManager.finalLevelNumber >= levelManager.currentLevel.levelNumber + 1) {
                    playLevel(levelManager.currentLevel.levelNumber + 1);
                } else {
                    MessageManager.getInstance().dispatchMessage(0, null, MessageType.gameOver);
                }
                unlockButtons();
            }
        }, 4);


    }

    private void addMessageListeners() {
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
        MessageManager.getInstance().addListener(this, MessageType.moreSpeed);
        MessageManager.getInstance().addListener(this, MessageType.restart);
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        switch (msg.message) {

            case MessageType.win:
                score++;
                scoreLabel.setText(binaryDisplay.getScore(score));
                guessed();
                break;
            case MessageType.lose:
                guessed();
                break;
            case MessageType.moreSpeed:
                recordSpeed += LEG_IMPULSE_SPEED;
                unlockButtons();
                break;
            case MessageType.restart:
                score = 0;
                playLevel(1);
                break;
        }
        return true;
    }


    // dirty hack as I keep getting a double tap when loading new levels
    protected void unlockButtons() {
        ImmutableArray<Entity> pictureEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(PictureComponent.class).get());
        for (Entity pictureEntity : pictureEntities) {
            pictureEntity.getComponent(PictureComponent.class).isTouched = false;
        }
    }

    protected void lockButtons() {
        ImmutableArray<Entity> pictureEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(PictureComponent.class).get());
        for (Entity pictureEntity : pictureEntities) {
            pictureEntity.getComponent(PictureComponent.class).isTouched = true;
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

    }



}
