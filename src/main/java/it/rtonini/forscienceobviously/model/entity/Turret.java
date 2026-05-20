package it.rtonini.forscienceobviously.model.entity;

import it.rtonini.forscienceobviously.model.Bullet;
import it.rtonini.forscienceobviously.model.Sprite;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Turret defense unit.
 */
public class Turret extends Sprite {
    private List<Bullet> bullets;
    private double lastShotTime;
    private static final double SHOOT_COOLDOWN = 1.0; // seconds

    public Turret(Image img, String nome) {
        super(img, nome);
        this.bullets = new ArrayList<>();
        this.lastShotTime = 0;
    }

    public void update(long now, List<Enemy> enemies) {
        // Simple shooting logic: if there's an enemy in range, shoot
        // For now, we just shoot periodically if there are any enemies
        if (enemies.isEmpty()) {
            return;
        }

        double seconds = now / 1_000_000_000.0;
        if (seconds - lastShotTime > SHOOT_COOLDOWN) {
            // Shoot towards the first enemy (simplified)
            Enemy target = enemies.get(0);
            Bullet bullet = new Bullet(new Image(getClass().getResource("/images/sprites/defenders/bullet.png").toExternalForm()), "Bullet");
            bullet.setX(getX() + getImg().getWidth() / 2);
            bullet.setY(getY() + getImg().getHeight() / 2);
            // In a real game, we would calculate direction and velocity
            bullets.add(bullet);
            lastShotTime = seconds;
        }

        // Update bullets (remove if out of bounds, etc.)
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            // Move bullet forward (simplified)
            bullet.setX(bullet.getX() + 5); // move right
            // Remove if off screen
            if (bullet.getX() > 800) {
                bullets.remove(i);
            }
        }
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}