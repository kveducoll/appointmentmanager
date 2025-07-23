module cpe121.group3 {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;

    opens cpe121.group3 to javafx.fxml;
    exports cpe121.group3;
}
