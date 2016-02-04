package uk.co.adeveloperabroad.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.resources.IResourceLoader;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.resources.ResourceManager;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;

public class LevelManager implements Disposable{

    private Array<Level> levels;
    private ItemWrapper root;

    public GameResourceManager getRm() {
        return rm;
    }

    public void setRm(GameResourceManager rm) {
        this.rm = rm;
    }

    public ItemWrapper getRoot() {
        return root;
    }

    public void setRoot(ItemWrapper root) {
        this.root = root;
    }

    private GameResourceManager rm;

    public Level currentLevel;
    public Sound mysterySound;


    public int finalLevelNumber;

    public LevelManager(Array levels) {
        this.levels = levels;
//        this.root = root;
//        this.rm = (GameResourceManager) rm;
        setFinalLevelNumber();
    }



    private void setFinalLevelNumber() {
        for (Level level : levels) {
            if (level.levelNumber > finalLevelNumber) {
                finalLevelNumber = level.levelNumber;
            }
        }
    }

    public void loadLevel(int levelNumber) {

        for (Level level : levels) {
            if (level.levelNumber == levelNumber) {
                currentLevel = level;
            }
        }
        loadSound();
        loadPictures();
    }

    public void loadSound() {

        if (mysterySound != null) {
            mysterySound.stop();
            mysterySound.dispose();
        }

        mysterySound = rm.soundManager.getSound(currentLevel.sound);

    }

    public void loadPictures() {

        loadPicture("pictureOne", currentLevel.pictureOne, currentLevel.correctAnswer);
        loadPicture("pictureTwo", currentLevel.pictureTwo,  currentLevel.correctAnswer);
        loadPicture("pictureThree", currentLevel.pictureThree, currentLevel.correctAnswer);
    }

    private void loadPicture(String pictureIdentifier, String newPictureName, String correctAnswer) {

        // get the picture entity from the composite
        Entity entity = root.getChild(pictureIdentifier).getEntity();
        NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
        Entity picture = nodeComponent.children.get(0);

        // get new image from the atlas
        TextureAtlas pack = rm.assetManager.get("orig/pack.atlas", TextureAtlas.class);
        TextureRegion nextRegion = pack.findRegion(newPictureName);
        picture.getComponent(TextureRegionComponent.class).region = nextRegion;

        // tell it if it is the correct answer.
        PictureComponent pictureComponent = ComponentRetriever.get(entity, PictureComponent.class);
        pictureComponent.isCorrectAnswer = pictureIdentifier.equals(correctAnswer);
    }

    @Override
    public void dispose() {

    }
}

