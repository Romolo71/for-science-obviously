package it.rtonini.forscienceobviously.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Base class for all game objects that have an image and a name.
 */
public class Sprite {
    protected Image img;
    protected String nome;
    protected ImageView spriteView;

    public Sprite(Image img, String nome) {
        this.img = img;
        this.nome = nome;
        this.spriteView = new ImageView(img);
    }

    public Image getImg() {
        return img;
    }

    public String getNome() {
        return nome;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ImageView getSpriteView() {
        return spriteView;
    }

    public double getX() {
        return spriteView.getX();
    }

    public double getY() {
        return spriteView.getY();
    }

    public void setX(double x) {
        spriteView.setX(x);
    }

    public void setY(double y) {
        spriteView.setY(y);
    }

    public javafx.geometry.Bounds getBoundsInParent() {
        return spriteView.getBoundsInParent();
    }
}