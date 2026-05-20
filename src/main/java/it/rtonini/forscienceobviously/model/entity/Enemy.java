package it.rtonini.forscienceobviously.model.entity;

import it.rtonini.forscienceobviously.model.Sprite;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Enemy extends Sprite {
    protected ImageView spriteView;
    protected double health;
    protected double maxHealth;
    protected double speed;
    protected boolean dead;
    protected boolean reachedExit;

    public Enemy(Image img, String nome) {
        super(img, nome);
        this.spriteView = new ImageView(img);
        this.dead = false;
        this.reachedExit = false;
    }

    public abstract void update(long now);

    public void takeDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isReachedExit() {
        return reachedExit;
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