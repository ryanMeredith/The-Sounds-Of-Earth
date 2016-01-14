package uk.co.adeveloperabroad;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;
import java.util.Comparator;

import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.systems.PictureSystem;

public class SoundsOfEarth extends ApplicationAdapter implements InputProcessor {

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

    private  MainItemComponent blueSqaure;
    private  MainItemComponent greenSqaure;
    private  MainItemComponent pinkSqaure;



	@Override
	public void create () {

        batch = new SpriteBatch();
		viewport = new FitViewport(160, 96);
		sceneLoader = new SceneLoader();
        sceneLoader.loadScene("MainScene", viewport);
        ItemWrapper root = new ItemWrapper(sceneLoader.getRoot());

        sceneLoader.addComponentsByTagName("picture", PictureComponent.class);
        ComponentRetriever.addMapper(PictureComponent.class);
        sceneLoader.getEngine().addSystem(new PictureSystem());

        blueSqaure = getCompositeLayers("blue", 0, root);
        greenSqaure = getCompositeLayers("green", 0, root);
        pinkSqaure = getCompositeLayers("pink", 0, root);

        greenSqaure.visible = false;
        pinkSqaure.visible = false;

        Gdx.input.setInputProcessor(this);

        Json json =  new Json();
        Array<Level> levels = json.fromJson(Array.class, Level.class, Gdx.files.internal("levels/levelResources"));
        levelManager = new LevelManager(levels, root);

        mysterySound = levelManager.mysterySound;
        soundId = mysterySound.loop();
        mysterySound.pause(soundId);

        TextureAtlas labelAtlas = new TextureAtlas(Gdx.files.internal("spriteAnimations/label/label.atlas"));
        labelAtlasRegions = new Array<TextureAtlas.AtlasRegion>(labelAtlas.getRegions());
        labelAtlasRegions.sort(new RegionComparator());
        labelAnimation = new Animation(LABEL_FPS, labelAtlasRegions, Animation.PlayMode.LOOP);

        TextureAtlas alienAtlas = new TextureAtlas(Gdx.files.internal("spriteAnimations/alien/alien.atlas"));
        alienAtlasRegions = new Array<TextureAtlas.AtlasRegion>(alienAtlas.getRegions());
        alienAtlasRegions.sort(new RegionComparator());
        alienAnimation = new Animation(ALIEN_FPS, alienAtlasRegions, Animation.PlayMode.LOOP);
    }


	@Override
	public void render() {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sceneLoader.getEngine().update(Gdx.graphics.getDeltaTime());

        recordSpeed -= Gdx.graphics.getDeltaTime()  * RECORD_DRAGSPEED;
        recordSpeed = MathUtils.clamp(recordSpeed,0, 10);

        if(recordSpeed == 0) {
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

        if (leg == 1) {
            blueSqaure.visible = false;
            greenSqaure.visible = true;
            pinkSqaure.visible = false;
        }

        if (leg == 2){
            blueSqaure.visible = false;
            greenSqaure.visible = false;
            pinkSqaure.visible = true;
        }

        if (leg == 3) {
            recordSpeed += LEG_IMPULSE_SPEED;
            blueSqaure.visible = true;
            greenSqaure.visible = false;
            pinkSqaure.visible = false;
        }
        animationTimeAlien = ALIEN_FPS * ALIEN_FRAMES_PER_LEG * (leg - 1);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) {
            if(nextLeg == 1) {
                nextLeg = 2;
                moveLeg(1);
                Gdx.app.log("Leg", "A");
            }
        }

        if (keycode == Input.Keys.S) {
            if(nextLeg == 2) {
                nextLeg = 3;
                moveLeg(2);
                Gdx.app.log("Leg", "B");
            }
        }

        if (keycode == Input.Keys.D) {
            if(nextLeg == 3) {
                nextLeg = 1;
                moveLeg(3);
                Gdx.app.log("Leg", "C");
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    protected MainItemComponent getCompositeLayers(String identifier, Integer depth, ItemWrapper root) {
        Entity entity = root.getChild(identifier).getEntity();
        NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
        Entity childEntity = nodeComponent.children.get(depth);
        return ComponentRetriever.get(childEntity, MainItemComponent.class);
    }

    private static class RegionComparator implements Comparator<TextureAtlas.AtlasRegion> {
        @Override
        public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
            return region1.name.compareTo(region2.name);
        }
    }
}

