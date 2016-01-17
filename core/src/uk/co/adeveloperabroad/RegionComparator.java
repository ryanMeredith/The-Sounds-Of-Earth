package uk.co.adeveloperabroad;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.Comparator;

/**
 * Created by snow on 17/01/16.
 */
public class RegionComparator implements Comparator<TextureAtlas.AtlasRegion> {
    @Override
    public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
        return region1.name.compareTo(region2.name);
    }

}
