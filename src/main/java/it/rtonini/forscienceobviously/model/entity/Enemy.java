package it.rtonini.forscienceobviously.model.entity;

import it.rtonini.forscienceobviously.model.Sprite;
import javafx.scene.image.Image;

/**
 * Abstract base class for all enemies.
 * Extends Sprite, which already provides spriteView, getX/Y, setX/Y, etc.
 */
public abstract class Enemy extends Sprite {

    protected double health;
    protected double maxHealth;
    protected double speed;
    protected boolean dead;
    protected boolean reachedExit;

    public Enemy(Image img, String nome) {
        super(img, nome);
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
}