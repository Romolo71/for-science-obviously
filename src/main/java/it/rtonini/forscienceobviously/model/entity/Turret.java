package it.rtonini.forscienceobviously.model.entity;

import it.rtonini.forscienceobviously.model.Bullet;
import it.rtonini.forscienceobviously.model.Sprite;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Turret extends Sprite {

    private final List<Bullet> pendingBullets = new ArrayList<>();
    private double lastShotTime = 0;

    private static final double SHOOT_COOLDOWN = 1.0;
    private static final double RANGE          = 200.0;
    private static final double BULLET_SPEED   = 6.0;
    private static final double DAMAGE         = 30.0;

    // Punti vita torretta
    private double health    = 100;
    private double maxHealth = 100;
    private boolean dead     = false;

    public Turret(Image img, String nome) {
        super(img, nome);
        spriteView.setFitWidth(32);
        spriteView.setFitHeight(32);
    }

    public void takeDamage(double dmg) {
        health -= dmg;
        if (health <= 0) {
            dead = true;
            health = 0;
        }
        // Rende la torretta sempre più trasparente man mano che viene danneggiata
        spriteView.setOpacity(0.3 + 0.7 * (health / maxHealth));
    }

    public boolean isDead() { return dead; }

    public void update(long now, List<Enemy> enemies) {
        if (dead || enemies.isEmpty()) return;
        double seconds = now / 1_000_000_000.0;
        if (seconds - lastShotTime < SHOOT_COOLDOWN) return;

        double cx = getX() + 16;
        double cy = getY() + 16;

        Enemy target   = null;
        double minDist = Double.MAX_VALUE;
        for (Enemy e : enemies) {
            double dx   = (e.getX() + 16) - cx;
            double dy   = (e.getY() + 16) - cy;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= RANGE && dist < minDist) {
                minDist = dist;
                target  = e;
            }
        }
        if (target == null) return;

        double dx  = (target.getX() + 16) - cx;
        double dy  = (target.getY() + 16) - cy;
        double len = Math.sqrt(dx * dx + dy * dy);
        double vx  = (dx / len) * BULLET_SPEED;
        double vy  = (dy / len) * BULLET_SPEED;

        Bullet bullet = new Bullet(null, "Bullet", DAMAGE, vx, vy);
        bullet.setX(cx - 4);
        bullet.setY(cy - 4);
        pendingBullets.add(bullet);
        lastShotTime = seconds;
    }

    public List<Bullet> consumePendingBullets() {
        List<Bullet> copy = new ArrayList<>(pendingBullets);
        pendingBullets.clear();
        return copy;
    }
}