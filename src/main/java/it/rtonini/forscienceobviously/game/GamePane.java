package it.rtonini.forscienceobviously.game;

import it.rtonini.forscienceobviously.model.Bullet;
import it.rtonini.forscienceobviously.model.entity.Enemy;
import it.rtonini.forscienceobviously.model.entity.Turret;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePane extends Pane {

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final double MAP_WIDTH       = 640;
    private static final double MAP_HEIGHT      = 480;
    private static final double PLAY_AREA_TOP   = 100.0;  // sotto la striscia personaggi
    private static final double DEFENSE_LINE_X  = 580.0;  // linea verticale arancione

    // ── Economia ──────────────────────────────────────────────────────────────
    private static final int TURRET_COST        = 50;
    private static final int KILL_REWARD        = 20;
    private static final int STARTING_MONEY     = 150;
    private int money = STARTING_MONEY;

    // ── Stato UI ──────────────────────────────────────────────────────────────
    private boolean placingTurret = false;
    private Label   moneyLabel;
    private Label   waveLabel;
    private Label   statusLabel;

    // ── Immagini ──────────────────────────────────────────────────────────────
    private Image TURRET_IMG;
    private Image HEADCRAB_IMG;

    private Image loadImg(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) return new Image(is);
        } catch (Exception ignored) {}
        System.err.println("WARNING: immagine non trovata: " + path);
        return null;
    }

    // ── Scene graph ───────────────────────────────────────────────────────────
    private final Group turretGroup = new Group();
    private final Group enemyGroup  = new Group();
    private final Group bulletGroup = new Group();

    // ── Game state ────────────────────────────────────────────────────────────
    private final List<Turret> turrets = new ArrayList<>();
    private final List<Enemy>  enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();

    private final WaveManager waveManager;
    private final Random      random = new Random();

    private long lastEnemySpawnTime = 0;
    private static final long ENEMY_SPAWN_INTERVAL = 1_800_000_000L;

    // ── Constructor ───────────────────────────────────────────────────────────
    public GamePane() {
        setPrefSize(MAP_WIDTH, MAP_HEIGHT);

        TURRET_IMG   = loadImg("/images/sprites/defenders/Turret.jpeg");
        HEADCRAB_IMG = loadImg("/images/sprites/attackers/headcrab.png");

        // Mappa
        Image mapImg = loadImg("/maps/lvl1.jpeg");
        if (mapImg != null) {
            ImageView mapView = new ImageView(mapImg);
            mapView.setFitWidth(MAP_WIDTH);
            mapView.setFitHeight(MAP_HEIGHT);
            getChildren().add(mapView);
        }

        // Linea arancione verticale (linea di difesa)
        Rectangle defenseLine = new Rectangle(DEFENSE_LINE_X, PLAY_AREA_TOP,
                3, MAP_HEIGHT - PLAY_AREA_TOP);
        defenseLine.setFill(Color.ORANGE);
        defenseLine.setMouseTransparent(true);

        getChildren().addAll(turretGroup, enemyGroup, bulletGroup, defenseLine);

        // HUD in alto a destra
        buildHUD();

        waveManager = new WaveManager(HEADCRAB_IMG);

        // Click per piazzare torretta
        setOnMouseClicked(event -> {
            if (!placingTurret) return;
            if (event.getButton() == MouseButton.PRIMARY) {
                double x = event.getX();
                double y = event.getY();
                if (y > PLAY_AREA_TOP && x < DEFENSE_LINE_X - 16) {
                    if (money >= TURRET_COST) {
                        placeTurret(x, y);
                        money -= TURRET_COST;
                        updateHUD();
                    } else {
                        statusLabel.setText("Soldi insufficienti!");
                    }
                }
                placingTurret = false;
                statusLabel.setText("");
                setCursor(javafx.scene.Cursor.DEFAULT);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                placingTurret = false;
                statusLabel.setText("");
                setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override public void handle(long now) {
                if (now - lastUpdate >= 16_666_666L) {
                    gameUpdate(now);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    // ── HUD ───────────────────────────────────────────────────────────────────
    private void buildHUD() {
        // Pannello in basso
        HBox hud = new HBox(12);
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setPadding(new Insets(6, 12, 6, 12));
        hud.setBackground(new Background(new BackgroundFill(
                Color.color(0, 0, 0, 0.65), new CornerRadii(8), Insets.EMPTY)));
        hud.setLayoutX(8);
        hud.setLayoutY(MAP_HEIGHT - 48);
        hud.setPrefWidth(MAP_WIDTH - 16);

        // Soldi
        moneyLabel = new Label(money + " $");
        moneyLabel.setTextFill(Color.GOLD);
        moneyLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");

        // Wave
        waveLabel = new Label("Wave 1");
        waveLabel.setTextFill(Color.LIGHTCYAN);
        waveLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold;");

        // Status
        statusLabel = new Label("");
        statusLabel.setTextFill(Color.TOMATO);
        statusLabel.setStyle("-fx-font-size:12px;");

        // Bottone piazza torretta
        Button buyBtn = new Button("Torretta (" + TURRET_COST + "$)");
        buyBtn.setStyle(
                "-fx-background-color: #e67e22; -fx-text-fill: white;" +
                        "-fx-font-weight: bold; -fx-background-radius: 6;" +
                        "-fx-padding: 4 12 4 12; -fx-cursor: hand;");
        buyBtn.setOnAction(e -> {
            if (money >= TURRET_COST) {
                placingTurret = true;
                statusLabel.setText("Clicca dove vuoi piazzare la torretta (tasto dx per annullare)");
                setCursor(javafx.scene.Cursor.CROSSHAIR);
            } else {
                statusLabel.setText("Soldi insufficienti!");
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        hud.getChildren().addAll(moneyLabel, waveLabel, spacer, statusLabel, buyBtn);
        getChildren().add(hud);
    }

    private void updateHUD() {
        moneyLabel.setText(money + " $");
        waveLabel.setText("Wave " + waveManager.getCurrentWave());
    }

    // ── Game loop ─────────────────────────────────────────────────────────────
    private void gameUpdate(long now) {
        waveManager.update(now);
        updateHUD();

        // Spawn nemici
        if (waveManager.canSpawnEnemy() && now - lastEnemySpawnTime > ENEMY_SPAWN_INTERVAL) {
            double spawnY = PLAY_AREA_TOP + 8 + random.nextDouble() * (MAP_HEIGHT - PLAY_AREA_TOP - 56);
            Enemy e = waveManager.spawnEnemy(spawnY);
            if (e != null) {
                enemies.add(e);
                enemyGroup.getChildren().add(e.getSpriteView());
                lastEnemySpawnTime = now;
            }
        }

        // Aggiorna nemici
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            if (e.isDead()) {
                enemyGroup.getChildren().remove(e.getSpriteView());
                enemies.remove(i);
                money += KILL_REWARD;
                updateHUD();
                continue;
            }

            // Controlla collisione con torrette (il nemico si ferma e attacca)
            Turret blocked = getTurretBlockingEnemy(e);
            if (blocked != null) {
                // Il nemico attacca la torretta
                ((Headcrab) e).attackTurret(blocked);
                if (blocked.isDead()) {
                    turretGroup.getChildren().remove(blocked.getSpriteView());
                    turrets.remove(blocked);
                    System.out.println("Torretta distrutta!");
                }
            } else if (e.getX() + 32 >= DEFENSE_LINE_X) {
                // Ha superato la linea senza bloccarsi (non dovrebbe succedere)
                enemyGroup.getChildren().remove(e.getSpriteView());
                enemies.remove(i);
            } else {
                e.update(now);
            }
        }

        // Torrette sparano
        for (Turret t : turrets) {
            t.update(now, enemies);
            for (Bullet b : t.consumePendingBullets()) {
                addBullet(b);
            }
        }

        // Proiettili
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(now);
            boolean hit = false;
            for (Enemy e : enemies) {
                if (b.getBoundsInParent().intersects(e.getBoundsInParent())) {
                    e.takeDamage(b.getDamage());
                    hit = true;
                    break;
                }
            }
            boolean oob = b.getX() < 0 || b.getX() > MAP_WIDTH
                    || b.getY() < PLAY_AREA_TOP || b.getY() > MAP_HEIGHT;
            if (hit || oob) {
                bulletGroup.getChildren().remove(b.getNode());
                bullets.remove(i);
            }
        }
    }

    // Restituisce la prima torretta che il nemico ha davanti (collisione)
    private Turret getTurretBlockingEnemy(Enemy e) {
        for (Turret t : turrets) {
            if (e.getBoundsInParent().intersects(t.getBoundsInParent())) {
                return t;
            }
        }
        return null;
    }

    // ── Piazza torretta ───────────────────────────────────────────────────────
    private void placeTurret(double x, double y) {
        Turret turret = new Turret(TURRET_IMG, "Turret");
        turret.setX(x - 16);
        turret.setY(y - 16);
        turrets.add(turret);
        turretGroup.getChildren().add(turret.getSpriteView());
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
        bulletGroup.getChildren().add(bullet.getNode());
    }

    // =========================================================================
    // WaveManager
    // =========================================================================
    private static class WaveManager {
        private final Image headcrabImg;
        private int  currentWave          = 0;
        private int  enemiesSpawnedInWave  = 0;
        private long lastWaveTime          = 0;
        private static final long WAVE_INTERVAL = 20_000_000_000L;

        WaveManager(Image img) {
            this.headcrabImg = img;
            startNewWave();
        }

        void update(long now) {
            if (lastWaveTime != 0
                    && now - lastWaveTime > WAVE_INTERVAL
                    && enemiesSpawnedInWave >= getEnemiesPerWave()) {
                startNewWave();
            }
        }

        private void startNewWave() {
            currentWave++;
            enemiesSpawnedInWave = 0;
            lastWaveTime = System.nanoTime();
            System.out.println("=== Wave " + currentWave + " (" + getEnemiesPerWave() + " nemici) ===");
        }

        boolean canSpawnEnemy() { return enemiesSpawnedInWave < getEnemiesPerWave(); }

        Enemy spawnEnemy(double spawnY) {
            if (!canSpawnEnemy()) return null;
            enemiesSpawnedInWave++;
            return new Headcrab(headcrabImg, spawnY);
        }

        int getCurrentWave() { return currentWave; }
        private int getEnemiesPerWave() { return 3 + currentWave; }
    }

    // =========================================================================
    // Headcrab
    // =========================================================================
    private static class Headcrab extends Enemy {

        private static final double MOVE_SPEED    = 1.5;
        private static final double ATTACK_DAMAGE = 0.3; // danno per frame alla torretta
        private boolean attacking = false;

        Headcrab(Image img, double spawnY) {
            super(img, "Headcrab");
            health    = 80;
            maxHealth = 80;
            speed     = MOVE_SPEED;

            spriteView.setFitWidth(32);
            spriteView.setFitHeight(32);
            spriteView.setX(-32);
            spriteView.setY(spawnY);
        }

        @Override
        public void update(long now) {
            if (dead) return;
            attacking = false;
            // Va dritto a destra
            spriteView.setX(spriteView.getX() + speed);
        }

        /** Chiamato dal game loop quando collide con una torretta */
        public void attackTurret(Turret t) {
            attacking = true;
            t.takeDamage(ATTACK_DAMAGE);
        }
    }
}