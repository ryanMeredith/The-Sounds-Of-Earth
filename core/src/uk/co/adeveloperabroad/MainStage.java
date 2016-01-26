package uk.co.adeveloperabroad;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.Controllers.AlienController;
import uk.co.adeveloperabroad.Controllers.AlienHeadController;
import uk.co.adeveloperabroad.Controllers.PlayAgainController;
import uk.co.adeveloperabroad.Controllers.SpeechController;
import uk.co.adeveloperabroad.Controllers.SpeedIndicatorController;
import uk.co.adeveloperabroad.Controllers.StylusController;
import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;
import uk.co.adeveloperabroad.components.WalkBoxComponent;
import uk.co.adeveloperabroad.systems.PictureSystem;
import uk.co.adeveloperabroad.Controllers.RecordLabelController;
import uk.co.adeveloperabroad.systems.WalkBoxSystem;

/**
 * Created by snow on 17/01/16.
 */
public class MainStage extends Stage implements Telegraph {

    private SceneLoader sceneLoader;
    private Viewport viewport;
    private LevelManager levelManager;


    private BinaryDisplay binaryDisplay = new BinaryDisplay();
    private LabelComponent scoreLabel;
    private LabelComponent trackLabel;
    private int score = 0;

    private float recordSpeed;
    private static float RECORD_DRAGSPEED = 0.8f;
    private static float LEG_IMPULSE_SPEED = 1.0f;

    private Entity stylus;
    private Entity speedIndicator;
    private Entity recordLabel;
    private Entity correct;
    private Entity incorrect;
    private Entity gameOverDialog;

    private Sound mysterySound;
    private Long soundId;

    private PictureSystem pictureSystem = new PictureSystem();
    private WalkBoxSystem walkBoxSystem = new WalkBoxSystem();

    public MainStage (Viewport viewport, SceneLoader sceneLoader) {
        this.viewport = viewport;
        this.sceneLoader = sceneLoader;

        sceneLoader.loadScene("MainScene", viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());

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


        root.getChild("alien").addScript(new AlienController());
        root.getChild("alienHead").addScript(new AlienHeadController());
        root.getChild("speech").addScript(new SpeechController());
        root.getChild("playAgain").addScript(new PlayAgainController());

        recordLabel = root.getChild("record").getEntity().add(new RecordSpeedComponent());
        root.getChild("record").addScript(new RecordLabelController());

        stylus = root.getChild("stylus").getEntity().add(new RecordSpeedComponent());
        root.getChild("stylus").addScript(new StylusController());

        speedIndicator = root.getChild("speedIndicator").getEntity().add(new RecordSpeedComponent());
        root.getChild("speedIndicator").addScript(new SpeedIndicatorController());

        addMessageListeners();

        Json json = new Json();
        Array<Level> levels = json.fromJson(Array.class, Level.class, Gdx.files.internal("levels/levelResources"));
        levelManager = new LevelManager(levels, root);

        playLevel(1);

    }

    @Override
    public void act() {

        super.act();
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());

        recordSpeed -= Gdx.graphics.getDeltaTime() * RECORD_DRAGSPEED;
        recordSpeed = MathUtils.clamp(recordSpeed, 0, 10);
        stylus.getComponent(RecordSpeedComponent.class).recordSpeed = recordSpeed;
        speedIndicator.getComponent(RecordSpeedComponent.class).recordSpeed = recordSpeed;
        recordLabel.getComponent(RecordSpeedComponent.class).recordSpeed = recordSpeed;

        if (recordSpeed == 0) {
            mysterySound.pause(soundId);
        } else {
            playSound(recordSpeed);
        }
    }

    @Override
    public void draw() {
        super.draw();
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());
    }

    protected void playLevel(int trackNumber) {
        sceneLoader.addComponentsByTagName("picture", PictureComponent.class);
        levelManager.loadLevel(trackNumber);
        trackLabel.setText(binaryDisplay.getTrack(trackNumber, levelManager.finalLevelNumber));
        loadMysterySound();
        startPositions();
    }


    private void playSound(float speed) {
        mysterySound.setPitch(soundId, speed * 0.2f);
        mysterySound.setVolume(soundId, 1.0f);
        mysterySound.resume(soundId);
    }


    protected void startPositions() {
        recordSpeed = 0;
        MessageManager.getInstance().dispatchMessage(0.0f, this, MessageType.startingPositions);
    }

    protected void loadMysterySound() {
        mysterySound = levelManager.mysterySound;
        soundId = mysterySound.loop();
        mysterySound.pause(soundId);
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
    public void dispose() {
        mysterySound.dispose();
    }



}
