package uk.co.adeveloperabroad.resourceManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by snow on 02/02/16.
 */
public class SoundManager implements Disposable {

    private HashMap<String, String> soundsInFile;
    private HashMap<String, Sound> sounds;

    public SoundManager(FileHandle handle) {
        soundsInFile = new HashMap<String, String>();
        sounds = new HashMap<String, Sound>();
        readSoundsToLoad(handle);
    }

    public Sound getSound(String soundName) {
        return sounds.get(soundName);
    }

    public HashMap<String, String> getSoundsInFile() {
        return soundsInFile;
    }


    private void readSoundsToLoad (FileHandle handle) {
        try {
            JsonReader reader = new JsonReader();
            JsonValue.JsonIterator it = reader.parse(handle).iterator();

            while (it.hasNext()) {
                JsonValue value = it.next();
                String name = value.getString("name");
                String location = value.getString("location");
                soundsInFile.put(name, location);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sync loading
    private void loadSoundData() {

        for (HashMap.Entry<String, String> entry : soundsInFile.entrySet()) {
            String name = entry.getKey();
            String location = entry.getValue();
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(location));
            sounds.put(name, sound);
        }
    }

    public void asyncLoadSoundData(String name, Sound sound) {
        sounds.put(name, sound);
    }

    public void removeSound(String soundName) {
        sounds.get(soundName).dispose();
        sounds.remove(soundName);
    }

    public String getSoundLocation(String soundName) {
        return soundsInFile.get(soundName);
    }

    @Override
      public void dispose() {
        Iterator<Sound> it = sounds.values().iterator();

        while (it.hasNext()) {
            it.next().dispose();
        }
        sounds.clear();
    }
}

