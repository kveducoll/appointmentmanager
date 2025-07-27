package cpe121.group3;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Node;

public class AboutController {

    @FXML
    private void closeAbout(ActionEvent event) {
        // Close the window/dialog
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
