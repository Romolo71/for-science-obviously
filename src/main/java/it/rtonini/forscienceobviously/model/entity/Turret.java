package it.rtonini.forscienceobviously.model.entity;

import it.rtonini.forscienceobviously.model.Bullet;
import it.rtonini.forscienceobviously.model.Sprite;
import javafx.scene.image.Image;

public class Turret extends Sprite {
    private int shots;
    private Bullet[] bullets;

    public Turret(Image img, String nome) {
        super(img, nome);
    }
}