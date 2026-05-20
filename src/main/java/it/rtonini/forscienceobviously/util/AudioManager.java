package it.rtonini.forscienceobviously.util;

/**
 * AudioManager placeholder - does nothing because JavaFX media is not available.
 */
public class AudioManager {
    private static AudioManager instance;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playMusic(String name, boolean loop) {
        // Do nothing - media not available
    }

    public void stopMusic() {
        // Do nothing
    }

    public void playSfx(String name) {
        // Do nothing
    }

    public void setVolume(double volume) {
        // Do nothing
    }

    public double getVolume() {
        return 0.0;
    }
}