package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;


import uk.co.adeveloperabroad.MessageType;

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
         winningMessages.add("Congratulations!\nYou are riding the\nlight wave of success!");
         winningMessages.add("Great win! \nThe galaxy's the limit!");
         winningMessages.add("Your a natural, \nhow did you get so good?");
         winningMessages.add("You're a rocket ship \nflying high!");
         winningMessages.add("Bang\nZoom\nStraight to the Moon");
         winningMessages.add("You are brighter\nthan the\nsun");
         winningMessages.add("You Rock-it\n HARD");
         winningMessages.add("You eclipse\n the competition");
         winningMessages.add("The cow jumped\n over the\nMoooooooon");
         winningMessages.add("You don't even\nfeel the pressure");
         winningMessages.add("Faster than\nthe speed\nof light");
         winningMessages.add("Enjoy a\nMars at \nlaunch time");
         winningMessages.add("Enjoy a cup of\n gravi-tea");
         winningMessages.add("Faster than\nthe speed\nof light");
         winningMessages.add("Hey you are\nlooking pretty\nstella");
         winningMessages.add("You're a star");
         winningMessages.add("You're out of\nthis world");
    }

    public void setLosingMessages() {
        losingMessages = new Array<String>(17);
        losingMessages.add("You'll go far\n the further the better");
        losingMessages.add("You are one\n UGLY\n alien");
        losingMessages.add("A half evolved\nstarsnail\ncould do better");
        losingMessages.add("Pathetic");
        losingMessages.add("Go back to the\nprimordial soup");
        losingMessages.add("You made me\nRun\nfor this?");
        losingMessages.add("I think you just\nbroke the law\nof gravity");
        losingMessages.add("you're back\nfrom outer space\nwith a sad look\non you're face");
        losingMessages.add("What does the\nspace turkey say?\nHubble Hubble");
        losingMessages.add("Did you forget\nto put money in\nthe parking meteor?");
        losingMessages.add("You are a\nLuna-tick");
        losingMessages.add("Have you\nbeen reading\ncomet books?");
        losingMessages.add("Space is out there\nnot between\n your ears");
        losingMessages.add("You've been\ndemoted\n like Pluto");
        losingMessages.add("You've just\ndrained the\natmosphere");
        losingMessages.add("Do you need\n some space?");
        losingMessages.add("Why so\n Sirius?");
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
