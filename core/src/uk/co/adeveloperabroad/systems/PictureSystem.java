package uk.co.adeveloperabroad.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.components.PictureComponent;

// borrowing heavily from buttonComponent (thanks azakhary)
public class PictureSystem extends IteratingSystem {

    public PictureSystem() {
        super(Family.all(PictureComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
       PictureComponent pictureComponent = ComponentRetriever.get(entity, PictureComponent.class);

        if(nodeComponent == null) return;

        for (int i = 0; i < nodeComponent.children.size; i++) {
            Entity childEntity = nodeComponent.children.get(i);
            MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
            ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);
            if(isTouched(entity)) {
                if(childZComponent.layerName.equals("normal")) {
                    childMainItemComponent.visible = false;
                }

                if(childZComponent.layerName.equals("right") && pictureComponent.isCorrectAnswer) {
                    childMainItemComponent.visible = true;
                }

                if(childZComponent.layerName.equals("wrong") && !pictureComponent.isCorrectAnswer) {
                    childMainItemComponent.visible = true;
                }


            } else {
                if(childZComponent.layerName.equals("normal")) {
                    childMainItemComponent.visible = true;
                }
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
        PictureComponent pictureComponent = entity.getComponent(PictureComponent.class);
        if(Gdx.input.isTouched()) {
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            Vector2 localCoordinates  = new Vector2(Gdx.input.getX(), Gdx.input.getY());

            TransformMathUtils.globalToLocalCoordinates(entity, localCoordinates);

            if(dimensionsComponent.hit(localCoordinates.x, localCoordinates.y)) {
                pictureComponent.setTouchState(true);
                return true;
            }
        }
        pictureComponent.setTouchState(false);
        return false;
    }

}
