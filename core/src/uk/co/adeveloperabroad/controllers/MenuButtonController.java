package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.utility.MessageType;

/**
 * Created by snow on 03/02/16.
 */
public class MenuButtonController implements IScript {

    private TintComponent tintComponent;
    private TintComponent tintComponent1;
    private TintComponent tintComponent2;
    private TintComponent tintComponent3;
    private float alpha = 0;

    private ButtonComponent buttonComponent;
    private String buttonName;

    private Vector2 localCoordinates;
    private DimensionsComponent dimensionsComponent;
    private Entity entity;



    @Override
    public void init(Entity entity) {

        this.entity = entity;
        dimensionsComponent = entity.getComponent(DimensionsComponent.class);
        tintComponent = entity.getComponent(TintComponent.class);
        NodeComponent nodeComponent = entity.getComponent(NodeComponent.class);
        tintComponent1 = nodeComponent.children.get(0).getComponent(TintComponent.class);
        tintComponent2 = nodeComponent.children.get(1).getComponent(TintComponent.class);
        tintComponent3 = nodeComponent.children.get(2).getComponent(TintComponent.class);


        tintComponent.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent2.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent3.color = tintComponent.color.set(1, 1,1 , alpha);

        MainItemComponent mainItemComponent = entity.getComponent(MainItemComponent.class);
        buttonName = mainItemComponent.itemIdentifier;

        localCoordinates = new Vector2(0,0);

    }

    @Override
    public void act(float delta) {
        alpha = MathUtils.clamp(alpha + (delta) * 0.5f, 0, 1);
        tintComponent.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);

        if (this.isTouched(entity)) {
            buttonPressed();
        }

    }

    private void buttonPressed() {

        if (buttonName.equals("intro")) {
            MessageManager.getInstance().dispatchMessage(null, MessageType.goToIntroduction);
        }

        if (buttonName.equals("play")) {
            MessageManager.getInstance().dispatchMessage(null, MessageType.goToGame);
        }

        if (buttonName.equals("about")) {
            MessageManager.getInstance().dispatchMessage(null, MessageType.goToAbout);
        }

        if (buttonName.equals("exit")) {
            Gdx.app.exit();
        }


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

    @Override
    public void dispose() {

    }
}
