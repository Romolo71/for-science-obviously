package it.rtonini.forscienceobviously.util;

/**
 * Game constants.
 */
public class Constants {
    // Game window
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;

    // Map
    public static final int TILE_SIZE = 64;
    public static final int MAP_WIDTH = 16; // in tiles
    public static final int MAP_HEIGHT = 12; // in tiles

    // Game loop
    public static final long NANO_SECOND = 1_000_000_000L;
    public static final int UPS = 60; // updates per second
    public static final long TIME_PER_UPDATE = NANO_SECOND / UPS;

    // Enemy
    public static final double ENEMY_SPEED_BASE = 50.0; // pixels per second
    public static final double ENEMY_HEALTH_BASE = 50.0;

    // Turret
    public static final double TURRET_RANGE_BASE = 150.0; // pixels
    public static final double TURRET_FIRE_RATE_BASE = 2.0; // shots per second
    public static final double TURRET_DAMAGE_BASE = 10.0;

    // Upgrade
    public static final double UPGRADE_MULTIPLIER = 0.2; // 20% increase per upgrade level

    // Audio
    public static final double MUSIC_VOLUME = 0.3;
    public static final double SFX_VOLUME = 0.5;

    // Private constructor to prevent instantiation
    private Constants() {}
}