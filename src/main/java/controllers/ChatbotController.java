package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.application.Platform;

public class ChatbotController {

    @FXML private TextArea chatArea;
    @FXML private TextField txtInput;

    @FXML
    public void initialize() {
        chatArea.appendText("Assistant IA Expert Java: Bonjour Soldat ! Je suis votre instructeur technique. \nPosez-moi n'importe quelle question sur Java ou la mission Battle.\n\n");
    }

    @FXML
    private void handleSend() {
        final String msg = txtInput.getText();
        if (msg != null && !msg.trim().isEmpty()) {
            chatArea.appendText("Moi: " + msg + "\n");
            txtInput.clear();
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try { Thread.sleep(500); } catch (Exception e) {}
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String resp = getExpertJavaResponse(msg);
                            chatArea.appendText("\nAssistant IA: " + resp + "\n\n");
                            chatArea.setScrollTop(Double.MAX_VALUE);
                        }
                    });
                }
            }).start();
        }
    }

    private String getExpertJavaResponse(String msg) {
        String l = msg.toLowerCase();
        if (l.contains("bonjour") || l.contains("salut")) return "Bonjour ! Je suis ravi de vous aider.";
        if (l.contains("jvm")) return "La JVM est le coeur de Java.";
        if (l.contains("héritage")) return "L'héritage permet la réutilisation du code.";
        return "Concept interessant sur '" + msg + "'. Voulez-vous un exemple ?";
    }
}
