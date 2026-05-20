package it.rtonini.forscienceobviously.model;

import javafx.scene.image.Image;

/**
 * Base class for all game objects that have an image and a name.
 */
public class Sprite {
    protected Image img;
    protected String nome;

    public Sprite(Image img, String nome) {
        this.img = img;
        this.nome = nome;
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
}