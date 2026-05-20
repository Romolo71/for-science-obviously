package it.rtonini.forscienceobviously.game;

import it.rtonini.forscienceobviously.model.Bullet;
import it.rtonini.forscienceobviously.model.entity.Turret;
import it.rtonini.forscienceobviously.model.Sprite;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The main game pane that contains the game logic and rendering.
 */
public class GamePane extends Pane {

    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 20; // in tiles
    private static final int MAP_HEIGHT = 15; // in tiles

    private final Image mapImage;
    private final ImageView mapView;
    private final Group turretGroup = new Group();
    private final Group enemyGroup = new Group();
    private final Group bulletGroup = new Group();

    private final List<Turret> turrets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    private final Path path;
    private final WaveManager waveManager;

    private long lastEnemySpawnTime;
    private static final long ENEMY_SPAWN_INTERVAL = 1_000_000_000; // 1 second in nanoseconds

    public GamePane() {
        // Load map image (assuming lvl1.jpeg is in resources/maps)
        mapImage = new Image(getClass().getResource("/maps/lvl1.jpeg").toExternalForm());
        mapView = new ImageView(mapImage);
        mapView.setFitWidth(MAP_WIDTH * TILE_SIZE);
        mapView.setFitHeight(MAP_HEIGHT * TILE_SIZE);

        // Define a simple path (from left to right, then down, then right, etc.)
        // This is just an example; you can define a more complex path.
        path = new Path();
        path.addWaypoint(0, MAP_HEIGHT / 2 * TILE_SIZE); // Start at left middle
        path.addWaypoint(MAP_WIDTH / 4 * TILE_SIZE, MAP_HEIGHT / 2 * TILE_SIZE);
        path.addWaypoint(MAP_WIDTH / 4 * TILE_SIZE, MAP_HEIGHT / 4 * TILE_SIZE);
        path.addWaypoint(MAP_WIDTH * 3 / 4 * TILE_SIZE, MAP_HEIGHT / 4 * TILE_SIZE);
        path.addWaypoint(MAP_WIDTH * 3 / 4 * TILE_SIZE, MAP_HEIGHT * 3 / 4 * TILE_SIZE);
        path.addWaypoint(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * 3 / 4 * TILE_SIZE); // Exit at right

        waveManager = new WaveManager(this);

        // Add the map and groups to the pane
        getChildren().addAll(mapView, turretGroup, enemyGroup, bulletGroup);

        // Set up mouse handling for placing turrets
        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double x = event.getX();
                double y = event.getY();
                // Snap to grid
                int gridX = (int) (x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2;
                int gridY = (int) (y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2;
                // Check if the cell is walkable (not on the path for simplicity)
                if (!path.isOnPath(gridX, gridY)) {
                    placeTurret(gridX, gridY);
                }
            }
        });

        // Start the game loop
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_666_666) { // ~60 FPS
                    update(now);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void update(long now) {
        // Spawn enemies based on wave manager
        waveManager.update(now);
        if (now - lastEnemySpawnTime > ENEMY_SPAWN_INTERVAL && waveManager.canSpawnEnemy()) {
            Enemy enemy = waveManager.spawnEnemy();
            if (enemy != null) {
                enemies.add(enemy);
                enemyGroup.getChildren().add(enemy.getSpriteView());
                lastEnemySpawnTime = now;
            }
        }

        // Update enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(now);
            if (enemy.isReachedExit()) {
                // Enemy reached the end, remove it and maybe reduce lives
                enemyGroup.getChildren().remove(enemy.getSpriteView());
                enemies.remove(i);
                // TODO: Handle lives
            } else if (enemy.isDead()) {
                enemyGroup.getChildren().remove(enemy.getSpriteView());
                enemies.remove(i);
                // TODO: Add bounty
            }
        }

        // Update turrets (shooting)
        for (Turret turret : turrets) {
            turret.update(now, enemies);
        }

        // Update bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(now);
            // Check for collision with enemies
            for (Enemy enemy : enemies) {
                if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    enemy.takeDamage(bullet.getDamage());
                    // Remove bullet
                    bulletGroup.getChildren().remove(bullet.getSpriteView());
                    bullets.remove(i);
                    break;
                }
            }
            // Remove bullet if it goes out of bounds
            if (bullet.getX() < 0 || bullet.getX() > getWidth() || bullet.getY() < 0 || bullet.getY() > getHeight()) {
                bulletGroup.getChildren().remove(bullet.getSpriteView());
                bullets.remove(i);
            }
        }
    }

    private void placeTurret(double x, double y) {
        // Create a turret sprite (using a placeholder image for now)
        Image turretImg = new Image(getClass().getResource("/images/sprites/defenders/turret1.png").toExternalForm());
        Turret turret = new Turret(turretImg, "Turret");
        turret.setX(x - turretImg.getWidth() / 2);
        turret.setY(y - turretImg.getHeight() / 2);
        turrets.add(turret);
        turretGroup.getChildren().add(turret.getSpriteView());
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
        bulletGroup.getChildren().add(bullet.getSpriteView());
    }

    /**
     * Simple path definition for enemies to follow.
     */
    private static class Path {
        private final List<Waypoint> waypoints = new ArrayList<>();

        public void addWaypoint(double x, double y) {
            waypoints.add(new Waypoint(x, y));
        }

        public boolean isOnPath(double x, double y) {
            // Simple check: if the point is near any waypoint, consider it on path (for simplicity)
            for (Waypoint wp : waypoints) {
                if (Math.abs(wp.x - x) < TILE_SIZE && Math.abs(wp.y - y) < TILE_SIZE) {
                    return true;
                }
            }
            return false;
        }

        public List<Waypoint> getWaypoints() {
            return waypoints;
        }

        private static class Waypoint {
            double x, y;

            Waypoint(double x, double y) {
                this.x = x;
                this.y = y;
            }
        }
    }

    /**
     * Manages waves of enemies.
     */
    private static class WaveManager {
        private final GamePane gamePane;
        private int currentWave = 0;
        private int enemiesSpawnedInWave = 0;
        private long lastWaveTime;
        private static final long WAVE_INTERVAL = 10_000_000_000L; // 10 seconds

        WaveManager(GamePane gamePane) {
            this.gamePane = gamePane;
            startNewWave();
        }

        public void update(long now) {
            if (now - lastWaveTime > WAVE_INTERVAL) {
                startNewWave();
            }
        }

        private void startNewWave() {
            currentWave++;
            enemiesSpawnedInWave = 0;
            lastWaveTime = System.nanoTime();
            System.out.println("Starting wave " + currentWave);
        }

        public boolean canSpawnEnemy() {
            return enemiesSpawnedInWave < getEnemiesPerWave();
        }

        public Enemy spawnEnemy() {
            if (!canSpawnEnemy()) {
                return null;
            }
            Enemy enemy = new Enemy(gamePane.path);
            enemiesSpawnedInWave++;
            return enemy;
        }

        private int getEnemiesPerWave() {
            return 3 + currentWave; // Increase enemies per wave
        }
    }

    /**
     * Enemy class that moves along the path.
     */
    private class Enemy {
        private final Path path;
        private final Sprite sprite;
        private final ImageView spriteView;
        private int currentWaypointIndex = 0;
        private double speed = 2.0; // pixels per frame
        private double health = 100;
        private final double maxHealth = 100;
        private boolean dead = false;
        private boolean reachedExit = false;

        Enemy(Path path) {
            this.path = path;
            // Load enemy sprite (placeholder)
            Image enemyImg = new Image(getClass().getResource("/images/sprites/attackers/enemy1.png").toExternalForm());
            sprite = new Sprite(enemyImg, "Enemy");
            spriteView = new ImageView(sprite.getImg());
            spriteView.setFitWidth(32);
            spriteView.setFitHeight(32);
            // Start at the first waypoint
            if (!path.getWaypoints().isEmpty()) {
                Waypoint wp = path.getWaypoints().get(0);
                spriteView.setX(wp.x - 16);
                spriteView.setY(wp.y - 16);
            }
        }

        public void update(long now) {
            if (dead || reachedExit) return;

            // Move towards the next waypoint
            if (currentWaypointIndex < path.getWaypoints().size()) {
                Waypoint target = path.getWaypoints().get(currentWaypointIndex);
                double dx = target.x - (spriteView.getX() + 16);
                double dy = target.y - (spriteView.getY() + 16);
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < speed) {
                    // Reached the waypoint
                    currentWaypointIndex++;
                    if (currentWaypointIndex >= path.getWaypoints().size()) {
                        reachedExit = true;
                        System.out.println("Enemy reached exit!");
                    }
                } else {
                    // Move towards the waypoint
                    spriteView.setX(spriteView.getX() + (dx / distance) * speed);
                    spriteView.setY(spriteView.getY() + (dy / distance) * speed);
                }
            }
        }

        public void takeDamage(double damage) {
            health -= damage;
            if (health <= 0) {
                dead = true;
                System.out.println("Enemy died!");
            }
        }

        public boolean isDead() {
            return dead;
        }

        public boolean isReachedExit() {
            return reachedExit;
        }

        public Sprite getSprite() {
            return sprite;
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

        public javafx.geometry.Bounds getBoundsInParent() {
            return spriteView.getBoundsInParent();
        }
    }
}