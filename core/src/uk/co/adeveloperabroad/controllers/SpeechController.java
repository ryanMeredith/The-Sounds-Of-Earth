package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;


import uk.co.adeveloperabroad.utility.MessageType;

/**
 * Created by snow on 17/01/16.
 */
public class SpeechController implements IScript, Telegraph {

    private Entity speech;
    private MainItemComponent mainItemComponent;
    private LabelComponent labelComponent;

    private Array<String> winningMessages;
    private Array<String> losingMessages;

    private String message;
    private int messageLength;
    private int currentPosition = 1;
    private float delay = 0.1f;
    private float time;

    @Override
    public void init(Entity entity) {

        speech = entity;

        mainItemComponent = entity.getComponent(MainItemComponent.class);
        mainItemComponent.visible = false;

        NodeComponent nodeComponent = entity.getComponent(NodeComponent.class);
        labelComponent = nodeComponent.children.get(1).getComponent(LabelComponent.class);
        labelComponent.setWrap(true);
        setWinningMessages();
        setLosingMessages();
        addListeners();

    }

    public void setWinningMessages() {
        winningMessages = new Array<String>(17);
         winningMessages.add("You're riding the light wave of success!");
         winningMessages.add("Great win! The galaxy's the limit!");
         winningMessages.add("Your a natural, how did you get so good?");
         winningMessages.add("You're a rocket ship flying high!");
         winningMessages.add("Bang Zoom Straight to the Moon");
         winningMessages.add("You are brighter than the sun");
         winningMessages.add("You Rock-it HARD");
         winningMessages.add("You eclipse the competition");
         winningMessages.add("The cow jumped over the Moooooooon");
         winningMessages.add("You don't even feel the pressure");
         winningMessages.add("Faster than the speed of light");
         winningMessages.add("Enjoy a Mars at launch time");
         winningMessages.add("Have a cup of gravi-tea");
         winningMessages.add("Faster than the speed of light");
         winningMessages.add("Hey you are looking pretty stella");
         winningMessages.add("You're a star");
         winningMessages.add("You're out of this world");
    }

    public void setLosingMessages() {
        losingMessages = new Array<String>(17);
        losingMessages.add("You'll go far  the further the better");
        losingMessages.add("You are one UGLY alien");
        losingMessages.add("A half evolved starsnail could do better");
        losingMessages.add("Pathetic");
        losingMessages.add("Go back to the primordial soup");
        losingMessages.add("You made me Run for this?");
        losingMessages.add("I think you just broke the law of gravity");
        losingMessages.add("You're as ugly as a human");
        losingMessages.add("What does the space turkey say? Hubble Hubble");
        losingMessages.add("Did you forget to put money in the parking meteor?");
        losingMessages.add("You are a Luna-tick");
        losingMessages.add("Have you been reading comet books?");
        losingMessages.add("Space is out there not between your ears");
        losingMessages.add("You've been demoted like Pluto");
        losingMessages.add("You've just drained the atmosphere");
        losingMessages.add("Do you need some space?");
        losingMessages.add("Why so Sirius?");
    }

    @Override
    public void act(float delta) {

        time += delta;
        if(currentPosition <= messageLength  && time > delay) {
            labelComponent.setText(message.substring(0, currentPosition));
            currentPosition ++;
            time = 0;
        }
    }

    @Override
    public void dispose() {

    }

    private String getWinningMessage(){
        return winningMessages.get(MathUtils.random(0, 16));
    }

    private String getLosingMessage() {
        return losingMessages.get(MathUtils.random(0,16));
    }

    private void addListeners() {
        MessageManager.getInstance().addListener(this, MessageType.win);
        MessageManager.getInstance().addListener(this, MessageType.lose);
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {

            case MessageType.win:
                message = getWinningMessage();
                currentPosition = 0;
                messageLength = message.length();
                mainItemComponent.visible = true;
                break;
            case MessageType.lose:
                message = getLosingMessage();
                currentPosition = 0;
                messageLength = message.length();
                mainItemComponent.visible = true;
                break;
            case MessageType.startingPositions:
                mainItemComponent.visible = false;
                labelComponent.setText("");

        }
        return true;
    }


}
