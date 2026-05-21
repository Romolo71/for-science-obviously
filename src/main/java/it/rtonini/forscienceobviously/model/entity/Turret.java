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

    private final List<Bullet> pendingBullets = new ArrayList<>();
    private double lastShotTime = 0;
    private static final double SHOOT_COOLDOWN = 1.0; // seconds
    private static final double RANGE = 150.0;
    private static final double BULLET_SPEED = 5.0;
    private static final double DAMAGE = 25.0;

    private Image bulletImg;

    public Turret(Image img, String nome) {
        super(img, nome);
        spriteView.setFitWidth(32);
        spriteView.setFitHeight(32);
        // Try to load bullet image; fallback to turret image if not found
        try {
            bulletImg = new Image(getClass().getResource("/images/sprites/defenders/bullet.png").toExternalForm());
        } catch (Exception e) {
            bulletImg = img;
        }
    }

    /**
     * Called each frame. Shoots at the nearest enemy in range.
     * Newly created bullets are added to pendingBullets; the GamePane
     * should call consumePendingBullets() and add them to the scene.
     */
    public void update(long now, List<Enemy> enemies) {
        if (enemies.isEmpty()) return;

        double seconds = now / 1_000_000_000.0;
        if (seconds - lastShotTime < SHOOT_COOLDOWN) return;

        // Find nearest enemy in range
        Enemy target = null;
        double minDist = Double.MAX_VALUE;
        double cx = getX() + spriteView.getFitWidth() / 2;
        double cy = getY() + spriteView.getFitHeight() / 2;

        for (Enemy e : enemies) {
            double dx = e.getX() - cx;
            double dy = e.getY() - cy;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= RANGE && dist < minDist) {
                minDist = dist;
                target = e;
            }
        }

        if (target == null) return;

        // Calculate direction
        double dx = target.getX() - cx;
        double dy = target.getY() - cy;
        double len = Math.sqrt(dx * dx + dy * dy);
        double vx = (dx / len) * BULLET_SPEED;
        double vy = (dy / len) * BULLET_SPEED;

        Bullet bullet = new Bullet(bulletImg, "Bullet", DAMAGE, vx, vy);
        bullet.setX(cx - 4);
        bullet.setY(cy - 4);
        pendingBullets.add(bullet);
        lastShotTime = seconds;
    }

    /** GamePane calls this every frame to pick up newly fired bullets. */
    public List<Bullet> consumePendingBullets() {
        List<Bullet> copy = new ArrayList<>(pendingBullets);
        pendingBullets.clear();
        return copy;
    }
}