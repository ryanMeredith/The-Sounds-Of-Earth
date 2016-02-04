package uk.co.adeveloperabroad.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.components.PictureComponent;
import uk.co.adeveloperabroad.resourceManagement.GameResourceManager;

public class LevelManager implements Disposable{

    private Array<Level> levels;
    private ItemWrapper root;

    private GameResourceManager rm;

    public Level currentLevel;
    public Sound mysterySound;

    public int finalLevelNumber;

    public LevelManager(Array levels, GameResourceManager rm) {
        this.levels = levels;
        this.rm =  rm;
        setFinalLevelNumber();
    }

    private String getSoundNameForLevel(int levelNumber) {
        for (Level level : levels) {
            if (level.levelNumber == levelNumber) {
                return level.sound;
            }
        }
        return null;
    }

    public void preLoadSound(int levelNumber) {

        if (levelNumber > finalLevelNumber) {
            levelNumber = 1;
        }
        rm.loadSound(getSoundNameForLevel(levelNumber));
    }



    private void setFinalLevelNumber() {
        for (Level level : levels) {
            if (level.levelNumber > finalLevelNumber) {
                finalLevelNumber = level.levelNumber;
            }
        }
    }

    public void loadLevel(int levelNumber) {

        preLoadSound(levelNumber + 1);
        unLoadOldSound(levelNumber);

        for (Level level : levels) {
            if (level.levelNumber == levelNumber) {
                currentLevel = level;
            }
        }
        setSound();
        loadPictures();
    }

    private void unLoadOldSound(int levelNumber) {

        if (mysterySound != null) {
            mysterySound.stop();
            mysterySound.dispose();
            mysterySound = null;
            rm.removeSound(getSoundNameForLevel(levelNumber - 1));
        }

    }

    public void setSound() {



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

    public void setRoot(ItemWrapper root) {
        this.root = root;
    }
}

