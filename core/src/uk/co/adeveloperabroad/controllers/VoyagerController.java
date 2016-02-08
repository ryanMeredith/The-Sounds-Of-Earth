package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import uk.co.adeveloperabroad.utility.MessageType;

/**
 * Created by snow on 03/02/16.
 */
public class VoyagerController implements IScript, Telegraph {

    private Entity entity;
    private TransformComponent transformComponent;
    private MainItemComponent mainItemComponent;

    private float startingPositionX;
    private float startingPositionY;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        transformComponent = entity.getComponent(TransformComponent.class);
        mainItemComponent = entity.getComponent(MainItemComponent.class);

        startingPositionX = transformComponent.x;
        startingPositionY = transformComponent.y;

        MessageManager.getInstance().addListener(this, MessageType.goToMenu);
    }

    @Override
    public void act(float delta) {

        if (transformComponent.x < 160) {
            transformComponent.x += 10 * delta;
            transformComponent.y += -5 * delta;
        } else if (transformComponent.x > 160 && mainItemComponent.visible) {
            mainItemComponent.visible = false;
            transformComponent.x = startingPositionX;
            transformComponent.y = startingPositionY;
        }

    }

    @Override
    public void dispose() {

    }

    public void reset() {
        mainItemComponent.visible = true;
        transformComponent.x = startingPositionX;
        transformComponent.y = startingPositionY;
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        if (msg.message == MessageType.goToMenu) {
            reset();
        }
        return true;
    }
}
