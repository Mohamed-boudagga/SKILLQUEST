package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class LoginSelectionController {

    @FXML
    private void handleUserLogin(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Node) {
            openWindowFromNode((Node)source, "/fxml/StudentDashboard.fxml", "SkillQuest - Espace Etudiant");
        }
    }

    @FXML
    private void handleAdminLogin(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Node) {
            openWindowFromNode((Node)source, "/fxml/AdminGames.fxml", "SkillQuest - Administration");
        }
    }

    @FXML
    private void handleCardUserClick(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Node) {
            openWindowFromNode((Node)source, "/fxml/StudentDashboard.fxml", "SkillQuest - Espace Etudiant");
        }
    }

    @FXML
    private void handleCardAdminClick(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof Node) {
            openWindowFromNode((Node)source, "/fxml/AdminGames.fxml", "SkillQuest - Administration");
        }
    }

    private void openWindowFromNode(Node node, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            node.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
