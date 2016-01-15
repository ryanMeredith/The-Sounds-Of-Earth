package uk.co.adeveloperabroad.components;

import com.badlogic.ashley.core.Component;


public class WalkBoxComponent implements Component {

    public boolean isTouched = false;

    public WalkBoxComponent() {

    }

    public void setTouchState(boolean isTouched) {
        this.isTouched = isTouched;
    }

}
