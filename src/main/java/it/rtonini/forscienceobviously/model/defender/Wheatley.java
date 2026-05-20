package it.rtonini.forscienceobviously.model.defender;

/**
 * Wheatley - Logistica
 * Controlla il numero e la varietà di torrette disponibili.
 */
public class Wheatley {
    private static Wheatley instance;
    private int upgradeLevel;
    private static final int MAX_LEVEL = 5;

    private Wheatley() {
        this.upgradeLevel = 0;
    }

    public static Wheatley getInstance() {
        if (instance == null) {
            instance = new Wheatley();
        }
        return instance;
    }

    public void upgrade() {
        if (upgradeLevel < MAX_LEVEL) {
            upgradeLevel++;
        }
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    /**
     * Returns the maximum number of turrets allowed based on upgrade level.
     * Base: 3 turrets, +1 per level.
     */
    public int getMaxTurrets() {
        return 3 + upgradeLevel;
    }

    /**
     * Returns a multiplier for turret variety (for future use).
     * For now, returns 1.0 + 0.1 * level.
     */
    public double getVarietyMultiplier() {
        return 1.0 + 0.1 * upgradeLevel;
    }

    private static final class WheatleyHolder {
        static final Wheatley INSTANCE = new Wheatley();
    }
}