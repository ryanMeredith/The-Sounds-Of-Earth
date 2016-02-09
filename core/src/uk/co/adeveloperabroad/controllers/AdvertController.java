package uk.co.adeveloperabroad.controllers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.label.LabelComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.TransformMathUtils;

import uk.co.adeveloperabroad.adMob.AdvertDisplay;

/**
 * Created by snow on 09/02/16.
 */
public class AdvertController implements IScript {

    AdvertDisplay advertDisplay;
    private Vector2 localCoordinates;
    private DimensionsComponent dimensionsComponent;
    private Entity entity;
    private LabelComponent labelComponent;
    private TintComponent tintComponent;
    private Boolean hasClicked = false;

    public AdvertController(AdvertDisplay advertDisplay) {
        this.advertDisplay = advertDisplay;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;
        localCoordinates = new Vector2(0,0);
        dimensionsComponent = entity.getComponent(DimensionsComponent.class);
        labelComponent = entity.getComponent(LabelComponent.class);
        tintComponent = entity.getComponent(TintComponent.class);
    }

    @Override
    public void act(float delta) {

        if (isTouched(entity) && !hasClicked) {
            advertDisplay.showAdvert();
            labelComponent.setText("Thankyou, you are amazing!!!");
            hasClicked = true;
            tintComponent.color = Color.WHITE;
        }
    }

    @Override
    public void dispose() {

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


}
