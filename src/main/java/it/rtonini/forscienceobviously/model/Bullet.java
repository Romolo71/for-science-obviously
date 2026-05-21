package it.rtonini.forscienceobviously.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * A projectile fired by a Turret.
 * If no image is provided, it renders as a small yellow circle.
 */
public class Bullet extends Sprite {

    private final double damage;
    private final double velocityX;
    private final double velocityY;

    // Used only when there is no image
    private Circle circle;

    public Bullet(Image img, String nome, double damage, double velocityX, double velocityY) {
        super(img, nome);
        this.damage    = damage;
        this.velocityX = velocityX;
        this.velocityY = velocityY;

        if (img != null) {
            spriteView.setFitWidth(8);
            spriteView.setFitHeight(8);
        } else {
            // Draw a yellow circle as bullet
            circle = new Circle(4, Color.YELLOW);
            circle.setStroke(Color.ORANGE);
        }
    }

    /** Move by velocity each frame. */
    public void update(long now) {
        if (circle != null) {
            circle.setCenterX(circle.getCenterX() + velocityX);
            circle.setCenterY(circle.getCenterY() + velocityY);
        } else {
            spriteView.setX(spriteView.getX() + velocityX);
            spriteView.setY(spriteView.getY() + velocityY);
        }
    }

    /**
     * Returns the JavaFX node to add to the scene.
     * Overrides Sprite.getSpriteView() — if using a circle, returns null from
     * spriteView but the caller must use getNode() instead.
     */
    public javafx.scene.Node getNode() {
        return circle != null ? circle : spriteView;
    }

    @Override
    public double getX() {
        return circle != null ? circle.getCenterX() : spriteView.getX();
    }

    @Override
    public double getY() {
        return circle != null ? circle.getCenterY() : spriteView.getY();
    }

    @Override
    public void setX(double x) {
        if (circle != null) circle.setCenterX(x);
        else spriteView.setX(x);
    }

    @Override
    public void setY(double y) {
        if (circle != null) circle.setCenterY(y);
        else spriteView.setY(y);
    }

    @Override
    public javafx.geometry.Bounds getBoundsInParent() {
        return circle != null ? circle.getBoundsInParent() : spriteView.getBoundsInParent();
    }

    public double getDamage() { return damage; }
}