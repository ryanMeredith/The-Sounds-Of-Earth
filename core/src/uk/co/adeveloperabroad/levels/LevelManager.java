package uk.co.adeveloperabroad.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.async.ThreadUtils;
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

    private int previousLevelNumber = 1;
    private int currentLevelNumber = 1;

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
        rm.loadSound(getSoundNameForLevel(levelNumber));
    }

    private void setFinalLevelNumber() {
        for (Level level : levels) {
            if (level.levelNumber > finalLevelNumber) {
                finalLevelNumber = level.levelNumber;
            }
        }
    }

    public void loadLevel() {

        if (previousLevelNumber != currentLevelNumber) {
            unLoadSound(previousLevelNumber);
        }

        preLoadSound(getNextLevelNumber());

        for (Level level : levels) {
            if (level.levelNumber == currentLevelNumber) {
                currentLevel = level;
            }
        }
        setSound();
        loadPictures();
    }

    public void unLoadSound(Integer levelNumber) {

        if (rm.assetManager.isLoaded(rm.soundManager.getSoundLocation(getSoundNameForLevel(levelNumber)))) {
            Gdx.app.log("levelManager: unload", getSoundNameForLevel(levelNumber));
            rm.removeSound(getSoundNameForLevel(levelNumber));
        }
    }

    public void setSound() {

        if (mysterySound != null) {
            mysterySound.stop();
            mysterySound = null;
        }

        loadLevelSound(); // if has not been done already // too late for android
        mysterySound = rm.soundManager.getSound(currentLevel.sound);

    }

    public void loadLevelSound() {
        // if the sound has not been loaded do an emergency load blocking thread until loaded.
        if (rm.soundManager.getSound(currentLevel.sound) == null) {
            rm.assetManager.load(rm.soundManager.getSoundsInFile().get(currentLevel.sound), Sound.class);
            rm.assetManager.finishLoadingAsset(rm.soundManager.getSoundsInFile().get(currentLevel.sound));
            rm.soundManager.asyncLoadSoundData(currentLevel.sound, rm.assetManager.get(rm.soundManager.getSoundsInFile().get(currentLevel.sound), Sound.class));
        }
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

    public Integer getNextLevelNumber() {

        int nextLevelNumber;
        if (currentLevelNumber == finalLevelNumber) {
            nextLevelNumber = 1;
        } else {
            nextLevelNumber = currentLevelNumber + 1;
        }
        return nextLevelNumber;
    }

    @Override
    public void dispose() {
        mysterySound.dispose();
    }

    public void setRoot(ItemWrapper root) {
        this.root = root;
    }

    public void setCurrentLevelNumber(Integer levelNumber) {
        previousLevelNumber = currentLevelNumber;
        currentLevelNumber = levelNumber;
    }

    public Integer getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public void loadNextLevel() {
        setCurrentLevelNumber(getNextLevelNumber());
    }
}

