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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.controllers.HomeButtonController;
import uk.co.adeveloperabroad.levels.Level;
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

    public GameScreen(Viewport viewport,  GameResourceManager rm) {
        this.viewport = viewport;
        sceneLoader = new SceneLoader(rm);

        Json json = new Json();
        Array<Level> levels = json.fromJson(Array.class, Level.class, Gdx.files.internal("levels/levelResources"));
        levelManager = new LevelManager(levels, rm);;
        levelManager.preLoadSound(1);

        sceneLoader.loadScene("MainScene", viewport);
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

        sceneLoader.addComponentsByTagName("picture", PictureComponent.class);
        sceneLoader.getEngine().addSystem(pictureSystem);

        sceneLoader.addComponentsByTagName("input", WalkBoxComponent.class);
        sceneLoader.getEngine().addSystem(walkBoxSystem);

        root.getChild("homeButton").addScript(new HomeButtonController());
        root.getChild("alien").addScript(new AlienController(
                rm.assetManager.get("spriteAnimations/walkPacked/walk.atlas", TextureAtlas.class)));

        root.getChild("alienHead").addScript(new AlienHeadController(
                rm.assetManager.get("spriteAnimations/headAnimPacked/head.atlas",
                        TextureAtlas.class), rm.soundManager.getSound("alienTalk")));

        recordLabel = root.getChild("record").getEntity().add(new RecordSpeedComponent());
        root.getChild("record").addScript(new RecordController(
                rm.assetManager.get("spriteAnimations/recordPacked/smallRecord.atlas",
                        TextureAtlas.class)));

        root.getChild("playAgain").addScript(new PlayAgainController());

        stylus = root.getChild("stylus").getEntity().add(new RecordSpeedComponent());
        root.getChild("stylus").addScript(new StylusController());

        speedIndicator = root.getChild("speedIndicator").getEntity().add(new RecordSpeedComponent());
        root.getChild("speedIndicator").addScript(new SpeedIndicatorController());

        addMessageListeners();

    }

    @Override
    public void show() {
        // done as early as possible for android;

        playLevel();
    }

    @Override
    public void render(float delta) {

        viewport.getCamera().update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT
                | GL20.GL_DEPTH_BUFFER_BIT
                | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV
                : 0));

        sceneLoader.getEngine().update(delta);

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
            MessageManager.getInstance().dispatchMessage(0, null, MessageType.goToMenu);
        }
        MessageManager.getInstance().update(Gdx.graphics.getDeltaTime());
    }

    protected void playLevel() {

        levelManager.loadLevel();
        trackLabel.setText(binaryDisplay.getTrack(levelManager.getCurrentLevelNumber(), levelManager.finalLevelNumber));
        loadMysterySound();
        unlockButtons();
        recordSpeed = 0;
    }

    protected void loadMysterySound() {
        soundId = levelManager.mysterySound.play();
        levelManager.mysterySound.setLooping(soundId, true);
        levelManager.mysterySound.pause(soundId);
    }

    private void playSound(float speed) {
        levelManager.mysterySound.setPitch(soundId, speed * 0.2f);
        levelManager.mysterySound.resume(soundId);
    }

    private void addMessageListeners() {
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
        MessageManager.getInstance().addListener(this, MessageType.moreSpeed);
        MessageManager.getInstance().addListener(this, MessageType.restart);
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        switch (msg.message) {

            case MessageType.win:
                score++;
                scoreLabel.setText(binaryDisplay.getScore(score));
                lockButtons();
                break;
            case MessageType.lose:
                lockButtons();
                break;
            case MessageType.moreSpeed:
                recordSpeed += LEG_IMPULSE_SPEED;
                break;
            case MessageType.restart:
                score = 0;
                levelManager.setCurrentLevelNumber(1);
                playLevel();
                break;
            case MessageType.startingPositions:

                if (levelManager.finalLevelNumber >= levelManager.currentLevel.levelNumber + 1) {
                    levelManager.loadNextLevel();
                    playLevel();
                } else {
                    MessageManager.getInstance().dispatchMessage(0, null, MessageType.gameOver);
                }

                break;
        }
        return true;
    }

    protected void unlockButtons() {
        ImmutableArray<Entity> pictureEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(PictureComponent.class).get());
        for (Entity pictureEntity : pictureEntities) {
            pictureEntity.getComponent(PictureComponent.class).isLocked = false;
        }

        ImmutableArray<Entity> walkEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(WalkBoxComponent.class).get());
        for (Entity walkEntity : walkEntities) {
            walkEntity.getComponent(WalkBoxComponent.class).isLocked = false;
        }
    }

    protected void lockButtons() {

        ImmutableArray<Entity> pictureEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(PictureComponent.class).get());
        for (Entity pictureEntity : pictureEntities) {
            pictureEntity.getComponent(PictureComponent.class).isLocked = true;
        }

        ImmutableArray<Entity> walkEntities =
                sceneLoader.getEngine().getEntitiesFor(Family.all(WalkBoxComponent.class).get());
        for (Entity walkEntity : walkEntities) {
            walkEntity.getComponent(WalkBoxComponent.class).isLocked = true;
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        // protecting androids sensitive sound pool
        levelManager.mysterySound.pause(soundId);
        levelManager.mysterySound = null;
        recordSpeed = 0;
        Gdx.app.log("game", "pause");
    }

    @Override
    public void resume() {
        levelManager.setSound();
        Gdx.app.log("game", "resume");
    }

    @Override
    public void hide() {
       levelManager.mysterySound.pause(soundId);
       levelManager.mysterySound = null;
    }


    @Override
    public void dispose() {

    }

}
