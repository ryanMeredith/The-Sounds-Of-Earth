package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.MessageType;

/**
 * Created by snow on 17/01/16.
 */
public class PlayAgainController implements IScript, Telegraph {

    private Entity playAgain;
    private MainItemComponent mainItemComponent;


    @Override
    public void init(Entity entity) {

        playAgain = entity;
        mainItemComponent = entity.getComponent(MainItemComponent.class);
        mainItemComponent.visible = false;

        addListeners();

    }


    @Override
    public void act(float delta) {

        if (isTouched(playAgain)) {
            MessageManager.getInstance().dispatchMessage(0, this, MessageType.restart);
        }

    }

    @Override
    public void dispose() {

    }


    private void addListeners() {
        MessageManager.getInstance().addListener(this, MessageType.gameOver);
        MessageManager.getInstance().addListener(this, MessageType.startingPositions);
    }

    private boolean isTouched(Entity entity) {

        if(Gdx.input.justTouched()) {
            DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
            Vector2 localCoordinates  = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            TransformMathUtils.globalToLocalCoordinates(entity, localCoordinates);

            if(dimensionsComponent.hit(localCoordinates.x, localCoordinates.y) &&
                    mainItemComponent.visible) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        switch (msg.message) {

            case MessageType.gameOver:
                mainItemComponent.visible = true;
                break;

            case MessageType.startingPositions:
                mainItemComponent.visible = false;
               break;

        }
        return true;
    }


}
