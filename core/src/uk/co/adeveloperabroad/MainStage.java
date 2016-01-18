package uk.co.adeveloperabroad;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.Controllers.DialogBoxController;
import uk.co.adeveloperabroad.Controllers.InfoBoxController;
import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;
import uk.co.adeveloperabroad.components.WalkBoxComponent;
import uk.co.adeveloperabroad.systems.PictureSystem;
import uk.co.adeveloperabroad.systems.RecordLabelController;
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

        uk.co.adeveloperabroad.Controllers.AlienController alienController = new uk.co.adeveloperabroad.Controllers.AlienController();
        root.getChild("alien").addScript(alienController);

        correct = root.getChild("correct").getEntity();
        incorrect = root.getChild("incorrect").getEntity();
        correct.getComponent(TintComponent.class).color = Color.CLEAR;
        incorrect.getComponent(TintComponent.class).color = Color.CLEAR;

        gameOverDialog = root.getChild("gameOverDialog").getEntity();
        gameOverDialog.getComponent(TintComponent.class).color = Color.CLEAR;
        DialogBoxController dialogBoxController = new DialogBoxController();
        root.getChild("gameOverDialog").addScript(dialogBoxController);


        InfoBoxController infoBoxController = new InfoBoxController();
        root.getChild("intro").addScript(infoBoxController);

        RecordLabelController recordLabelController = new RecordLabelController();
        recordLabel = root.getChild("recordLabel").getEntity().add(new RecordSpeedComponent());
        root.getChild("recordLabel").addScript(recordLabelController);

        uk.co.adeveloperabroad.Controllers.StylusController stylusController = new uk.co.adeveloperabroad.Controllers.StylusController();
        stylus = root.getChild("stylus").getEntity().add(new RecordSpeedComponent());
        root.getChild("stylus").addScript(stylusController);

        uk.co.adeveloperabroad.Controllers.SpeedIndicatorController speedIndicatorController = new uk.co.adeveloperabroad.Controllers.SpeedIndicatorController();
        speedIndicator = root.getChild("speedIndicator").getEntity().add(new RecordSpeedComponent());
        root.getChild("speedIndicator").addScript(speedIndicatorController);

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
                correct.getComponent(TintComponent.class).color = Color.CLEAR;
                incorrect.getComponent(TintComponent.class).color = Color.CLEAR;
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
                correct.getComponent(TintComponent.class).color = Color.WHITE;

                score++;
                scoreLabel.setText(binaryDisplay.getScore(score));
                guessed();
                break;
            case MessageType.lose:
                incorrect.getComponent(TintComponent.class).color = Color.WHITE;
                guessed();
                break;
            case MessageType.moreSpeed:
                recordSpeed += LEG_IMPULSE_SPEED;
                unlockButtons();
                break;
            case MessageType.restart:
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
