package com.zarczukp.world;

import org.apache.commons.math3.linear.RealVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Figure {
    List<RealVector> points;

    public Figure() {
        this.points = new ArrayList<>();
    }

    public List<RealVector> getPoints() {
        return points;
    }

    public void addPoint(RealVector point) {
        this.points.add(point);
    }

}
