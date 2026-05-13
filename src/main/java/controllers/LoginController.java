package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    public void versEtudiant(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SelectionNiveau.fxml"));
            Parent root = loader.load();
            
            SelectionNiveauController controller = loader.getController();
            controller.setRole(false); // Mode Étudiant : verrouillage actif
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void versAdmin(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdminDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHoverIn(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(1.05);
        st.setToY(1.05);
        st.play();
        
        // On peut aussi booster l'opacité ou l'effet de lueur ici via code
    }

    @FXML
    public void handleHoverOut(MouseEvent event) {
        VBox card = (VBox) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }
}
