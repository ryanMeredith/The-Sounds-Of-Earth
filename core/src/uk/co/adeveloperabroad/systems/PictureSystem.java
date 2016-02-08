package uk.co.adeveloperabroad.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.ai.msg.MessageManager;

import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.utility.MessageType;
import uk.co.adeveloperabroad.components.PictureComponent;

// borrowing heavily from buttonComponent (thanks azakhary)
public class PictureSystem extends IteratingSystem {

    private Vector2 localCoordinates;

    public PictureSystem() {
        super(Family.all(PictureComponent.class).get());
        localCoordinates = new Vector2(0,0);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
        PictureComponent pictureComponent = ComponentRetriever.get(entity, PictureComponent.class);

        if(nodeComponent == null) return;

        // don't proceed if picture is locked
        if (pictureComponent.isLocked) {
            return;
        }


        for (int i = 0; i < nodeComponent.children.size; i++) {

                Entity childEntity = nodeComponent.children.get(i);
                MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
                ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);

                if(isTouched(entity)) {

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

                } else {

                    if(childZComponent.layerName.equals("wrong")) {
                        childMainItemComponent.visible = false;
                    }
                    if(childZComponent.layerName.equals("right")) {
                        childMainItemComponent.visible = false;
                    }
                }

        }


    }

    private boolean isTouched(Entity entity) {

        if(Gdx.input.justTouched()) {
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);

            // if polygon component not created for this entity set.
            if (dimensionsComponent.polygon == null) {
                PolygonComponent polygonComponent = ComponentRetriever.get(entity, PolygonComponent.class);
                dimensionsComponent.setPolygon(polygonComponent);
            }

            TransformMathUtils.globalToLocalCoordinates(entity, localCoordinates.set(Gdx.input.getX(), Gdx.input.getY()));

            if(dimensionsComponent.hit(localCoordinates.x, localCoordinates.y)) {
                return true;
            }
        }
        return false;
    }

}
