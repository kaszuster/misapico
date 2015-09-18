package de.thkoeln.mosapico.data.model;

import java.awt.*;

/**
 * Created by szuster on 11.09.2015.
 */
public class ImageDimension extends Dimension {

    private int segments;

    public ImageDimension(int width, int height, int segments) {
        super(width, height);
        this.segments = segments;
    }

    public int getSegments() {
        return segments;
    }

    public void setSegments(int segments) {
        this.segments = segments;
    }

    public boolean equals(int width, int height) {
        return getWidth() == width && getHeight() == height;
    }
}
