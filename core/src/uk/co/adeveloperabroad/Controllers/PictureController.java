package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.MessageType;
import uk.co.adeveloperabroad.components.PictureComponent;

/**
 * Created by snow on 17/01/16.
 */
public class PictureController implements IScript {

    Entity entity;
    NodeComponent nodeComponent;
    PictureComponent pictureComponent;

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
        pictureComponent = ComponentRetriever.get(entity, PictureComponent.class);
    }

    @Override
    public void act(float delta) {

        if(isTouched(entity) && !pictureComponent.isTouched) {
            setWinOrLoseState();
        } else {
            setStartingState();
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

    private void setWinOrLoseState() {

        if (nodeComponent == null) {
            return;
        }

        for (int i = 0; i < nodeComponent.children.size; i++) {

            Entity childEntity = nodeComponent.children.get(i);
            MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
            ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);


            if(childZComponent.layerName.equals("right")
                    && pictureComponent.isCorrectAnswer) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.win);
                childMainItemComponent.visible = true;
            }

            if(childZComponent.layerName.equals("wrong")
                    && !pictureComponent.isCorrectAnswer) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.lose);
                childMainItemComponent.visible = true;
            }
        }
    }

    private void setStartingState() {

        for (int i = 0; i < nodeComponent.children.size; i++) {

            Entity childEntity = nodeComponent.children.get(i);
            MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
            ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);

            if (childZComponent.layerName.equals("wrong")) {
                childMainItemComponent.visible = false;
            }
            if (childZComponent.layerName.equals("right")) {
                childMainItemComponent.visible = false;
            }
        }
    }
}
