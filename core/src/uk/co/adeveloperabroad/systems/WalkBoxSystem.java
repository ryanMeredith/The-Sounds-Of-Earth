package uk.co.adeveloperabroad.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.PolygonComponent;
import com.uwsoft.editor.renderer.components.ZIndexComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.CustomVariables;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.utility.MessageType;
import uk.co.adeveloperabroad.components.WalkBoxComponent;

public class WalkBoxSystem extends IteratingSystem implements Telegraph {

    public int nextLeg = 1;
    private CustomVariables customVariables;
    private Vector2 localCoordinates;
    private String legNumber;

    public WalkBoxSystem() {
        super(Family.all(WalkBoxComponent.class).get());
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
        customVariables = new CustomVariables();
        localCoordinates = new Vector2(1,1);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        NodeComponent nodeComponent = ComponentRetriever.get(entity, NodeComponent.class);
        MainItemComponent mainItemComponent = ComponentRetriever.get(entity, MainItemComponent.class);
//        customVariables.loadFromString(mainItemComponent.customVars);
// ^ this seems to be causing a memory leak and I cannot work out why. ^
//        legNumber = customVariables.getIntegerVariable("legNumber");


        if(nodeComponent == null) return;

        if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
            hideKeyOptions(nodeComponent);
        }

        if(isTouched(entity)) {
//            Gdx.app.log("foot", "touched");

            if (mainItemComponent.customVars.equals("legNumber:1") && nextLeg == 1) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.leg1);
                nextLeg = 2;
            }

            if (mainItemComponent.customVars.equals("legNumber:2") && nextLeg == 2) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.leg2);
                nextLeg = 3;
            }

            if (mainItemComponent.customVars.equals("legNumber:3") && nextLeg == 3) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.leg3);
                nextLeg = 1;
            }

        } else if(isKeyDown()) {
//            Gdx.app.log("foot", "key down");

            if ((Gdx.input.isKeyJustPressed(Input.Keys.A) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
                    && nextLeg == 1) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.leg1);
                nextLeg = 2;
            }

            if ((Gdx.input.isKeyJustPressed(Input.Keys.S) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.UP))
                    && nextLeg == 2) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.leg2);
                nextLeg = 3;
            }

            if ((Gdx.input.isKeyJustPressed(Input.Keys.D) ||
                    Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                    && nextLeg == 3) {
                MessageManager.getInstance().dispatchMessage(0.0f, null, MessageType.leg3);
                nextLeg = 1;
            }

        } else {

            if (mainItemComponent.customVars.equals("legNumber:1") && nextLeg == 1) {
                nextButton(nodeComponent);
            } else if (mainItemComponent.customVars.equals("legNumber:1") && nextLeg != 1) {
                makeTransparent(nodeComponent);
            }

            if (mainItemComponent.customVars.equals("legNumber:2") && nextLeg == 2) {
                nextButton(nodeComponent);
            } else if (mainItemComponent.customVars.equals("legNumber:2") && nextLeg != 2) {
                makeTransparent(nodeComponent);
            }

            if (mainItemComponent.customVars.equals("legNumber:3") && nextLeg == 3) {
                nextButton(nodeComponent);
            } else if (mainItemComponent.customVars.equals("legNumber:3") && nextLeg != 3) {
                makeTransparent(nodeComponent);
            }
        }
    }

    private void makeTransparent(NodeComponent nodeComponent) {
        for (int i = 0; i < nodeComponent.children.size; i++) {

            Entity childEntity = nodeComponent.children.get(i);
            MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
            ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);

            if(childZComponent.layerName.equals("transparent") ) {
                childMainItemComponent.visible = true;
            }

            if(childZComponent.layerName.equals("keyboard") ) {
                childMainItemComponent.visible = false;
            }

            if(childZComponent.layerName.equals("tablet") ) {
                childMainItemComponent.visible = false;
            }

        }
    }

    private void hideKeyOptions(NodeComponent nodeComponent) {
        for (int i = 0; i < nodeComponent.children.size; i++) {

            Entity childEntity = nodeComponent.children.get(i);
            MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
            ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);

            if(childZComponent.layerName.equals("keyboard") ) {
                childMainItemComponent.visible = false;
            }
        }
    }

    private void nextButton(NodeComponent nodeComponent) {
        for (int i = 0; i < nodeComponent.children.size; i++) {

            Entity childEntity = nodeComponent.children.get(i);
            MainItemComponent childMainItemComponent = ComponentRetriever.get(childEntity, MainItemComponent.class);
            ZIndexComponent childZComponent = ComponentRetriever.get(childEntity, ZIndexComponent.class);

            if(childZComponent.layerName.equals("transparent") ) {
                childMainItemComponent.visible = false;
            }

            if(childZComponent.layerName.equals("keyboard")
                && (Gdx.app.getType() == Application.ApplicationType.Desktop)

                    ) {
                childMainItemComponent.visible = true;
            }

            if(childZComponent.layerName.equals("tablet") ) {
                childMainItemComponent.visible = true;
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

    private Boolean isKeyDown() {


        if (Gdx.input.isKeyJustPressed(Input.Keys.A)
               || Gdx.input.isKeyJustPressed(Input.Keys.S)
                || Gdx.input.isKeyJustPressed(Input.Keys.D)
                || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)
                || Gdx.input.isKeyJustPressed(Input.Keys.UP)
                || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)

                ){
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMessage(Telegram msg) {

        if (msg.message == MessageType.startingPositions) {
            nextLeg = 1;
        }
        return true;
    }
}
