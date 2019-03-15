package com.zarczukp.world;

import java.util.ArrayList;
import java.util.List;

public class World {
    List<Figure> figures;

    public List<Figure> getFigures() {
        return figures;
    }

    public void addFigure(Figure figure) {
        this.figures.add(figure);
    }

    public World() {
        this.figures = new ArrayList<>();
    }
}
