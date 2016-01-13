package uk.co.adeveloperabroad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class LevelManager {

    private Array<Level> levels;
    public Level currentLevel;

    public Sound mysterySound;



    public LevelManager(Array levels) {
        this.levels = levels;
        loadLevel(1);
    }

    public void loadLevel(int levelNumber) {

        for (Level level : levels) {
            if (level.levelNumber == levelNumber) {
                currentLevel = level;
            }
        }

        loadSound();

    }

    public void loadSound() {
        mysterySound = Gdx.audio.newSound(Gdx.files.internal(currentLevel.sound));
    }

}

