package uk.co.adeveloperabroad.Controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import uk.co.adeveloperabroad.components.RecordSpeedComponent;

public class SpeedIndicatorController implements IScript {

    private Entity speedIndicator;
    private RecordSpeedComponent recordSpeedComponent;
    private TintComponent tintComponentAtom;
    private TintComponent tintComponentLine;
    private NodeComponent nodeComponent;
    private TransformComponent speedLineTransform;
    private float startingPosition;

    @Override
    public void init(Entity entity) {
        speedIndicator = entity;
        recordSpeedComponent = entity.getComponent(RecordSpeedComponent.class);
        nodeComponent = entity.getComponent(NodeComponent.class);

        Entity atom = nodeComponent.children.get(0);
        Entity speedLine = nodeComponent.children.get(1);

        tintComponentAtom = atom.getComponent(TintComponent.class);
        tintComponentLine = speedLine.getComponent(TintComponent.class);
        speedLineTransform = speedLine.getComponent(TransformComponent.class);
        startingPosition = speedLineTransform.x;

    }

    @Override
    public void act(float delta) {

        changeColour();
        speedLineTransform.x = startingPosition + recordSpeedComponent.recordSpeed * 0.5f;
    }

    protected void changeColour() {
        float power =  Math.abs(MathUtils.cos((recordSpeedComponent.recordSpeed * 0.1f) * MathUtils.PI));
        setColor(power);
    }

    public void setColor(float power) {

        //0 full green, 1 full red

        float blue = 0.0f;
        float red = 0.0f;
        float green = 0.0f;

        if (0.0f <= power || power < 0.5f ){//   first, green stays at 100%, red raises to 100%
             green = 1.0f;
             red = 2 * power;
        }

        if (0.5f <= power || power < 1f ){ //then red stays at 100%, green decays
             red = 1.0f;
             green = 1.0f - 2.0f * (power - 0.5f);
        }
        tintComponentAtom.color.set(red, green, blue, 1f);
        tintComponentLine.color.set(red, green, blue, 1f);
    }


    @Override
    public void dispose() {

    }
}
