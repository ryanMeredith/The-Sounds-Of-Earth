package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.MessageType;

/**
 * Created by snow on 17/01/16.
 */
public class InfoBoxController implements IScript {

    private Entity dialogBox;
    private TransformComponent transformComponent;
    private TintComponent tintComponent;
    private NodeComponent nodeComponent;

    @Override
    public void init(Entity entity) {
        dialogBox = entity;
        transformComponent = entity.getComponent(TransformComponent.class);
        tintComponent = entity.getComponent(TintComponent.class);

        nodeComponent = entity.getComponent(NodeComponent.class);
        showDialog();
    }

    private void hideDialog() {
        for (int i = 0; i < nodeComponent.children.size; i++) {
            Entity childEntity = nodeComponent.children.get(i);
            childEntity.getComponent(TintComponent.class).color = Color.CLEAR;
        }

        transformComponent.x = 500;
        transformComponent.y = 500;
    }

    private void showDialog() {
        for (int i = 0; i < nodeComponent.children.size; i++) {
            Entity childEntity = nodeComponent.children.get(i);
            childEntity.getComponent(TintComponent.class).color = Color.WHITE;
        }

        transformComponent.x = 60;
        transformComponent.y = 50;
    }

    @Override
    public void act(float delta) {

        if (isTouched(dialogBox)) {
            hideDialog();
        }

    }

    @Override
    public void dispose() {

    }


    private boolean isTouched(Entity entity) {

        if(Gdx.input.justTouched()) {
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            Vector2 localCoordinates  = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            TransformMathUtils.globalToLocalCoordinates(entity, localCoordinates);

            if(dimensionsComponent.hit(localCoordinates.x, localCoordinates.y)) {
                return true;
            }
        }
        return false;
    }
}
