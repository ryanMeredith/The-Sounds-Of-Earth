package uk.co.adeveloperabroad;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;

import com.uwsoft.editor.renderer.components.additional.ButtonComponent;

import uk.co.adeveloperabroad.Components.PictureComponent;

public class PictureSystem extends IteratingSystem {

    private ComponentMapper<ButtonComponent> buttonMapper;
    private ButtonComponent buttonComponent;

    public PictureSystem() {
        super(Family.all(PictureComponent.class).get());
        buttonMapper = ComponentMapper.getFor(ButtonComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        buttonComponent = buttonMapper.get(entity);
//        Gdx.app.log("touched", Boolean.toString(buttonComponent.isTouched) );
        //buttonComponent.isTouched = true;
    }

}
