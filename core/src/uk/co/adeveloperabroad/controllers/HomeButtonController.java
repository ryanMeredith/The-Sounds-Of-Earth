package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.utility.MessageType;

/**
 * Created by snow on 08/02/16.
 */
public class HomeButtonController implements IScript {

    private Vector2 localCoordinates;
    private DimensionsComponent dimensionsComponent;
    private Entity entity;

    @Override
    public void init(Entity entity) {
        localCoordinates = new Vector2(0,0);
        dimensionsComponent = entity.getComponent(DimensionsComponent.class);
        this.entity = entity;
    }

    @Override
    public void act(float delta) {

        if (isTouched(entity)) {
            MessageManager.getInstance().dispatchMessage(0, null, MessageType.goToMenu);
        }

    }

    @Override
    public void dispose() {

    }

    private boolean isTouched(Entity entity) {

        if(Gdx.input.justTouched()) {
            localCoordinates.set(Gdx.input.getX(), Gdx.input.getY());
            TransformMathUtils.globalToLocalCoordinates(entity, localCoordinates);
            if(dimensionsComponent.hit(localCoordinates.x, localCoordinates.y)) {
                return true;
            }
        }
        return false;
    }
}
