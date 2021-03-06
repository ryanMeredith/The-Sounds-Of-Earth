package uk.co.adeveloperabroad.resourceManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.utils.Disposable;
import com.uwsoft.editor.renderer.resources.ResourceManager;
import java.io.File;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by snow on 29/01/16.
 */
public class AsynchronousResourceManager extends ResourceManager implements Disposable {

    public boolean isCurrentlyLoading = false;
    public AssetManager assetManager;
    public SoundManager soundManager;

    private HashSet<String> soundsToLoad;

    public float loadedAtStart = 0.0f;
    public float percentageLoaded = 0.0f;

    public AsynchronousResourceManager() {
        assetManager = new AssetManager();
        soundManager = new SoundManager(Gdx.files.internal("sound/sounds.json"));
        soundsToLoad = new HashSet<String>();
    }

    public void loadSounds() {

        setLoadingFlag();

        for (HashMap.Entry<String, String> sound : soundManager.getSoundsInFile().entrySet()) {
            soundsToLoad.add(sound.getKey());
            assetManager.load(sound.getValue(), Sound.class);
        }

    }

    public void loadMusic(String musicLocation) {
        setLoadingFlag();
        assetManager.load(musicLocation, Music.class);
    }

    public void loadSound(String soundName) {

        if (soundManager.getSound(soundName) == null) {
            setLoadingFlag();
            soundsToLoad.add(soundName);
            assetManager.load(soundManager.getSoundsInFile().get(soundName), Sound.class);
        }

    }

    public void removeSound(String soundName) {
        soundManager.removeSound(soundName);
        assetManager.unload(soundManager.getSoundLocation(soundName));
    }

    // fast method to make sure we have all project data as these should be small
    public void initAllSceneData() {

        loadProjectVO();
        for (int i = 0; i < projectVO.scenes.size(); i++) {
            loadSceneVO(projectVO.scenes.get(i).sceneName);
            scheduleScene(projectVO.scenes.get(i).sceneName);
        }
        prepareAssetsToLoad();
        loadFonts();
    }

    // Load individual scene data.
    public void initSceneData(String sceneName) {

        for (int i = 0; i < projectVO.scenes.size(); i++) {
            if (projectVO.scenes.get(i).sceneName.equals(sceneName)){
                loadSceneVO(projectVO.scenes.get(i).sceneName);
                scheduleScene(projectVO.scenes.get(i).sceneName);
            }
        }
    }

    @Override
    public void loadAtlasPack() {
        if (mainPack == null) {
            setLoadingFlag();
            assetManager.load(packResolutionName + File.separator + "pack.atlas", TextureAtlas.class);
        }
    }

    public void loadAnimationAtlasPack(String location) {
        setLoadingFlag();
        assetManager.load(location, TextureAtlas.class);
    }

    public void loadSpriteAnimation(String animationName) {
        setLoadingFlag();
        //empty existing ones that are not scheduled to load
        spriteAnimNamesToLoad.add(animationName);

        for (String name : spriteAnimNamesToLoad) {
            assetManager.load(packResolutionName + File.separator
                    + spriteAnimationsPath + File.separator +
                    name + File.separator + name + ".atlas", TextureAtlas.class);
        }
    }


    public void setLoadingFlag() {
        isCurrentlyLoading = true;
        loadedAtStart = assetManager.getLoadedAssets();
    }


    public boolean update() {

        if (isCurrentlyLoading) {

            boolean finishedLoading = assetManager.update();
            updatePercentageLoaded();
            if (finishedLoading) {
                Gdx.app.log("loaded", assetManager.getAssetNames().toString());
                if (isCurrentlyLoading) {
                    postLoad();
                }
                return true;
            }
        }
        return false;
    }

    // gives resources to overlap2D
    protected void postLoad() {

        if (mainPack == null) {
            mainPack = assetManager.get(packResolutionName + File.separator + "pack.atlas", TextureAtlas.class);
            // relies on images inside pack will work out a better way to do this later.
            loadParticleEffects();
        }

        for (String name : spriteAnimNamesToLoad) {
            TextureAtlas animAtlas = assetManager.get(packResolutionName +
                    File.separator + spriteAnimationsPath + File.separator +
                    name + File.separator + name +
                    ".atlas", TextureAtlas.class);
            spriteAnimations.put(name, animAtlas);
        }
        Gdx.app.log("sounds to load", soundsToLoad.toString());
        for (String soundName : soundsToLoad) {

            soundManager.asyncLoadSoundData(
                    soundName,
                    assetManager.get(soundManager.getSoundsInFile().get(soundName), Sound.class)
            );
        }
        soundsToLoad.clear();

        isCurrentlyLoading = false;
    }

    // calculates percentage loaded
    protected void updatePercentageLoaded() {
        percentageLoaded = Math.min(1,
                (assetManager.getLoadedAssets() - loadedAtStart) /
                        (assetManager.getLoadedAssets() + assetManager.getQueuedAssets() - loadedAtStart)
        );
    }

    public void dispose() {
        super.dispose();
        assetManager.dispose();
        soundManager.dispose();
    }


}
