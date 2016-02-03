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
public class SoundManager implements Disposable{

    private HashMap<String, String> soundsToLoad;
    private HashMap<String, Sound> sounds;

    public SoundManager(FileHandle handle) {
        soundsToLoad = new HashMap<String, String>();
        sounds = new HashMap<String, Sound>();
        readSoundsToLoad(handle);
    }

    public Sound getSound(String soundName) {
        Sound sound = sounds.get(soundName);

        if (sound != null) {
            return sound;
        }

        return null;
    }

    public HashMap<String, String> getSoundsToLoad() {
        return soundsToLoad;
    }


    private void readSoundsToLoad (FileHandle handle) {
        try {
            JsonReader reader = new JsonReader();
            JsonValue.JsonIterator it = reader.parse(handle).iterator();

            while (it.hasNext()) {
                JsonValue value = it.next();
                String name = value.getString("name");
                String location = value.getString("location");
                soundsToLoad.put(name, location);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sync loading
    private void loadSoundData() {

        for (HashMap.Entry<String, String> entry : soundsToLoad.entrySet()) {
            String name = entry.getKey();
            String location = entry.getValue();
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(location));
            sounds.put(name, sound);
        }
    }

    public void asyncLoadSoundData(String name, Sound sound) {
        sounds.put(name, sound);
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

