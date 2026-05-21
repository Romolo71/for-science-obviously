package it.rtonini.forscienceobviously.model;

import javafx.scene.image.Image;

public class Bullet extends Sprite {

    private double damage;
    private double velocityX;
    private double velocityY;

    public Bullet(Image img, String nome, double damage, double velocityX, double velocityY) {
        super(img, nome);
        this.damage = damage;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        spriteView.setFitWidth(8);
        spriteView.setFitHeight(8);
    }

    public void update(long now) {
        spriteView.setX(spriteView.getX() + velocityX);
        spriteView.setY(spriteView.getY() + velocityY);
    }

    public double getDamage() {
        return damage;
    }
}