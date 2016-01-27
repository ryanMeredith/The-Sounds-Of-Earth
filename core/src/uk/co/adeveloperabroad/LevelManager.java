package uk.co.adeveloperabroad;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TextureRegionComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import uk.co.adeveloperabroad.components.PictureComponent;

public class LevelManager {

    private Array<Level> levels;
    private ItemWrapper root;

    public Level currentLevel;
    public Sound mysterySound;

    public int finalLevelNumber;

    public LevelManager(Array levels, ItemWrapper root) {
        this.levels = levels;
        this.root = root;
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
            mysterySound.dispose();
        }

        mysterySound = Gdx.audio.newSound(Gdx.files.internal(currentLevel.sound));

    }

    public void loadPictures() {

        loadPicture("pictureOne", currentLevel.pictureOne, currentLevel.correctAnswer);
        loadPicture("pictureTwo", currentLevel.pictureTwo,  currentLevel.correctAnswer);
        loadPicture("pictureThree", currentLevel.pictureThree, currentLevel.correctAnswer);
    }

    private void loadPicture(String pictureIdentifier, String newPictureName, String correctAnswer) {
        Entity entity = root.getChild(pictureIdentifier).getEntity();
        NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
        Entity oldPicture = nodeComponent.children.get(0);
        Entity newPicture = root.getChild(newPictureName).getEntity();
        TextureRegionComponent newTexture = ComponentRetriever.get(newPicture, TextureRegionComponent.class);
        oldPicture.add(newTexture);

        PictureComponent pictureComponent = ComponentRetriever.get(entity, PictureComponent.class);
        pictureComponent.isCorrectAnswer = pictureIdentifier.equals(correctAnswer);
    }

}

