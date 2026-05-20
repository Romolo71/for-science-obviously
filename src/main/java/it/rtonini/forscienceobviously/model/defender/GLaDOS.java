package it.rtonini.forscienceobviously.model.defender;

import it.rtonini.forscienceobviously.model.Sprite;
import javafx.scene.image.Image;

/**
 * GLaDOS - Controllo centrale (Singleton)
 * Permette di posizionare le torrette
 */
public class GLaDOS extends Sprite {
    private static GLaDOS instance;

    private GLaDOS(Image img) {
        super(img, "GLaDOS");
    }

    public static GLaDOS getInstance(Image img) {
        if (instance == null) {
            instance = new GLaDOS(img);
        }
        return instance;
    }

    // Methods for turret placement would go here
}