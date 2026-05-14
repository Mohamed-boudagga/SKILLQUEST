package controllers.gestionTestCertif;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LevelUnlockedController {
    @FXML private Label lbMessage;
    public void setMessage(String message) { lbMessage.setText(message); }
    @FXML public void closeWindow(ActionEvent event) { ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); }
}
