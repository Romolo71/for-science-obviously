package it.rtonini.forscienceobviously.model.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Map {

    private String background;
    private String nome;
    private GraphicsContext gc;

    public Map(GraphicsContext gc, String nome, String background) {
        this.gc = gc;
        this.nome = nome;
        this.background = background;
    }
}
