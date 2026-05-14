module it.rtonini.forscienceobviously {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens it.rtonini.forscienceobviously to javafx.fxml;
    exports it.rtonini.forscienceobviously;
}