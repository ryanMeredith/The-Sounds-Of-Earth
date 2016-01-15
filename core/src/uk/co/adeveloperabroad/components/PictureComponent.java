package uk.co.adeveloperabroad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Array;

import uk.co.adeveloperabroad.MessageType;
import uk.co.adeveloperabroad.SoundsOfEarth;

public class PictureComponent implements Component, Telegraph {

    public boolean isCorrectAnswer = false;
    public boolean isTouched = false;
    public boolean hasGuessed = false;
    private Array<PictureListener> listeners = new Array<PictureListener>();

    public PictureComponent() {
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
       hasGuessed = true;
        System.out.println("message handled");
        return true;
    }

    public interface PictureListener {
        public void touchUp();
        public void touchDown();
        public void clicked();
    }

    public void addListener(PictureListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PictureListener listener) {
        listeners.removeValue(listener, true);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void setTouchState(boolean isTouched) {
        if(this.isTouched == false && isTouched == true) {
            for(int i = 0; i < listeners.size; i++) {
                listeners.get(i).touchDown();
            }
        }
        if(this.isTouched == true && isTouched == false) {
            for(int i = 0; i < listeners.size; i++) {
                listeners.get(i).touchUp();
                listeners.get(i).clicked();
            }
        }
        this.isTouched = isTouched;
    }


}
