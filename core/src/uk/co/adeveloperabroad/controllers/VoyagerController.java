package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

/**
 * Created by snow on 03/02/16.
 */
public class VoyagerController implements IScript {

    private Entity entity;
    private TransformComponent transformComponent;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        transformComponent = entity.getComponent(TransformComponent.class);

    }

    @Override
    public void act(float delta) {

        if (transformComponent.x < 160) {
            transformComponent.x += 10 * delta;
            transformComponent.y += -5 * delta;
        }

    }

    @Override
    public void dispose() {

    }
}
