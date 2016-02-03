package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

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

    @Override
    public void init(Entity entity) {
        tintComponent = entity.getComponent(TintComponent.class);
        NodeComponent nodeComponent = entity.getComponent(NodeComponent.class);
        tintComponent1 = nodeComponent.children.get(0).getComponent(TintComponent.class);
        tintComponent2 = nodeComponent.children.get(1).getComponent(TintComponent.class);
        tintComponent3 = nodeComponent.children.get(2).getComponent(TintComponent.class);


        tintComponent.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent2.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent3.color = tintComponent.color.set(1, 1,1 , alpha);

        buttonComponent = new ButtonComponent();
        entity.add(buttonComponent);

        MainItemComponent mainItemComponent = entity.getComponent(MainItemComponent.class);
        buttonName = mainItemComponent.itemIdentifier;

    }

    @Override
    public void act(float delta) {
        alpha = MathUtils.clamp(alpha + (delta) * 0.5f, 0, 1);
        tintComponent.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);
        tintComponent1.color = tintComponent.color.set(1, 1, 1, alpha);

        if (buttonComponent.isTouched) {
            buttonPressed();
        }

    }

    private void buttonPressed() {

        if (buttonName.equals("intro")) {
            System.out.println("intro");
        }

        if (buttonName.equals("play")) {
            MessageManager.getInstance().dispatchMessage(null, MessageType.playGame);
        }

        if (buttonName.equals("about")) {
            System.out.println("intro");
        }
    }

    @Override
    public void dispose() {

    }
}
