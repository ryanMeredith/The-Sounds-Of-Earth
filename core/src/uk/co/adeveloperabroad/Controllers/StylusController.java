package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;

import uk.co.adeveloperabroad.MessageType;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;

/**
 * Created by snow on 16/01/16.
 */
public class StylusController implements IScript, Telegraph {

    private Entity stylus;
    private TransformComponent transformComponent;
    private RecordSpeedComponent recordSpeedComponent;
    private TintComponent tintComponent;
    private float startingPosition;
    private float endingPosition = -2.0f;

    @Override
    public void init(Entity entity) {
        stylus = entity;
        transformComponent = entity.getComponent(TransformComponent.class);
        recordSpeedComponent = entity.getComponent(RecordSpeedComponent.class);
        tintComponent = entity.getComponent(TintComponent.class);
        startingPosition = transformComponent.x;
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
    }

    @Override
    public void act(float delta) {

        transformComponent.x = MathUtils.clamp(transformComponent.x
                + (delta * recordSpeedComponent.recordSpeed * 0.10f),startingPosition, endingPosition);


        if (transformComponent.x == endingPosition) {
            MessageManager.getInstance().dispatchMessage(0.0f, this, MessageType.timeout);
        }

        if (transformComponent.x > (endingPosition - 8) && transformComponent.x <= endingPosition) {
            float colourChange = 0.2f * MathUtils.sin(transformComponent.x * 4.0f);
            tintComponent.color = tintComponent.color.sub(0f, colourChange, colourChange, 0f);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean handleMessage(Telegram msg) {

        if (msg.message == MessageType.startingPositions) {
            transformComponent.x = startingPosition;
            tintComponent.color = tintComponent.color.set(Color.WHITE);
        }
        return true;
    }
}
