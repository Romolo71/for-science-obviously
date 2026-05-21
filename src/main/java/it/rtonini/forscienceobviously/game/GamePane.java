package it.rtonini.forscienceobviously.game;

import it.rtonini.forscienceobviously.model.Bullet;
import it.rtonini.forscienceobviously.model.entity.Enemy;
import it.rtonini.forscienceobviously.model.entity.Turret;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * The main game pane that contains the game logic and rendering.
 */
public class GamePane extends Pane {

    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 20;  // in tiles
    private static final int MAP_HEIGHT = 15; // in tiles

    private final Image mapImage;
    private final ImageView mapView;

    private final Group turretGroup = new Group();
    private final Group enemyGroup  = new Group();
    private final Group bulletGroup = new Group();

    private final List<Turret> turrets = new ArrayList<>();
    private final List<Enemy>  enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    private final Path path;
    private final WaveManager waveManager;

    private long lastEnemySpawnTime = 0;
    private static final long ENEMY_SPAWN_INTERVAL = 1_000_000_000L; // 1 second

    public GamePane() {
        mapImage = new Image(getClass().getResource("/maps/lvl1.jpeg").toExternalForm());
        mapView  = new ImageView(mapImage);
        mapView.setFitWidth(MAP_WIDTH  * TILE_SIZE);
        mapView.setFitHeight(MAP_HEIGHT * TILE_SIZE);

        // Define path waypoints
        path = new Path();
        path.addWaypoint(0,                              (MAP_HEIGHT / 2.0) * TILE_SIZE);
        path.addWaypoint((MAP_WIDTH / 4.0)  * TILE_SIZE, (MAP_HEIGHT / 2.0) * TILE_SIZE);
        path.addWaypoint((MAP_WIDTH / 4.0)  * TILE_SIZE, (MAP_HEIGHT / 4.0) * TILE_SIZE);
        path.addWaypoint((MAP_WIDTH * 3/4.0)* TILE_SIZE, (MAP_HEIGHT / 4.0) * TILE_SIZE);
        path.addWaypoint((MAP_WIDTH * 3/4.0)* TILE_SIZE, (MAP_HEIGHT * 3/4.0) * TILE_SIZE);
        path.addWaypoint(MAP_WIDTH          * TILE_SIZE, (MAP_HEIGHT * 3/4.0) * TILE_SIZE);

        waveManager = new WaveManager(path);

        getChildren().addAll(mapView, turretGroup, enemyGroup, bulletGroup);

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double x = event.getX();
                double y = event.getY();
                int gridX = (int)(x / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2;
                int gridY = (int)(y / TILE_SIZE) * TILE_SIZE + TILE_SIZE / 2;
                if (!path.isOnPath(gridX, gridY)) {
                    placeTurret(gridX, gridY);
                }
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_666_666L) {
                    update(now);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    // -------------------------------------------------------------------------
    // Game loop
    // -------------------------------------------------------------------------

    private void update(long now) {
        // Spawn enemies
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
            if (enemy.isReachedExit() || enemy.isDead()) {
                enemyGroup.getChildren().remove(enemy.getSpriteView());
                enemies.remove(i);
            }
        }

        // Update turrets — collect newly fired bullets
        for (Turret turret : turrets) {
            turret.update(now, enemies);
            for (Bullet b : turret.consumePendingBullets()) {
                addBullet(b);
            }
        }

        // Update bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(now);

            boolean hit = false;
            for (Enemy enemy : enemies) {
                if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    enemy.takeDamage(bullet.getDamage());
                    hit = true;
                    break;
                }
            }

            boolean outOfBounds = bullet.getX() < 0 || bullet.getX() > getWidth()
                    || bullet.getY() < 0 || bullet.getY() > getHeight();

            if (hit || outOfBounds) {
                bulletGroup.getChildren().remove(bullet.getSpriteView());
                bullets.remove(i);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void placeTurret(double x, double y) {
        Image turretImg = new Image(
                getClass().getResource("/images/sprites/defenders/turret1.png").toExternalForm());
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

    // -------------------------------------------------------------------------
    // Path — static, no reference to outer instance needed
    // -------------------------------------------------------------------------

    static class Path {

        private final List<double[]> waypoints = new ArrayList<>();

        public void addWaypoint(double x, double y) {
            waypoints.add(new double[]{x, y});
        }

        public boolean isOnPath(double x, double y) {
            for (double[] wp : waypoints) {
                if (Math.abs(wp[0] - x) < TILE_SIZE && Math.abs(wp[1] - y) < TILE_SIZE) {
                    return true;
                }
            }
            return false;
        }

        public List<double[]> getWaypoints() {
            return waypoints;
        }
    }

    // -------------------------------------------------------------------------
    // WaveManager — static (receives Path as constructor arg, no outer ref)
    // -------------------------------------------------------------------------

    private static class WaveManager {

        private final Path path;
        private int currentWave = 0;
        private int enemiesSpawnedInWave = 0;
        private long lastWaveTime = 0;
        private static final long WAVE_INTERVAL = 10_000_000_000L; // 10 seconds

        WaveManager(Path path) {
            this.path = path;
            startNewWave();
        }

        public void update(long now) {
            if (lastWaveTime != 0 && now - lastWaveTime > WAVE_INTERVAL) {
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
            if (!canSpawnEnemy()) return null;
            Enemy enemy = new BasicEnemy(path);
            enemiesSpawnedInWave++;
            return enemy;
        }

        private int getEnemiesPerWave() {
            return 3 + currentWave;
        }
    }

    // -------------------------------------------------------------------------
    // BasicEnemy — concrete implementation of the abstract Enemy from model
    // -------------------------------------------------------------------------

    private static class BasicEnemy extends Enemy {

        private final Path path;
        private int currentWaypointIndex = 0;

        BasicEnemy(Path path) {
            super(null, "Enemy"); // img loaded inside
            this.path = path;

            try {
                Image img = new Image(
                        BasicEnemy.class.getResource("/images/sprites/attackers/enemy1.png")
                                .toExternalForm());
                setImg(img);
                spriteView.setImage(img);
            } catch (Exception ignored) {}

            spriteView.setFitWidth(32);
            spriteView.setFitHeight(32);
            health    = 100;
            maxHealth = 100;
            speed     = 2.0;

            if (!path.getWaypoints().isEmpty()) {
                double[] wp = path.getWaypoints().get(0);
                spriteView.setX(wp[0] - 16);
                spriteView.setY(wp[1] - 16);
            }
        }

        @Override
        public void update(long now) {
            if (dead || reachedExit) return;

            List<double[]> waypoints = path.getWaypoints();
            if (currentWaypointIndex >= waypoints.size()) {
                reachedExit = true;
                return;
            }

            double[] target = waypoints.get(currentWaypointIndex);
            double cx = spriteView.getX() + 16;
            double cy = spriteView.getY() + 16;
            double dx = target[0] - cx;
            double dy = target[1] - cy;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < speed) {
                currentWaypointIndex++;
                if (currentWaypointIndex >= waypoints.size()) {
                    reachedExit = true;
                    System.out.println("Enemy reached exit!");
                }
            } else {
                spriteView.setX(spriteView.getX() + (dx / distance) * speed);
                spriteView.setY(spriteView.getY() + (dy / distance) * speed);
            }
        }
    }
}