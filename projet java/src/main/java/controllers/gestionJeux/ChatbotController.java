package controllers.gestionJeux;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatbotController {

    @FXML private TextArea chatArea;
    @FXML private TextField txtInput;

    @FXML public void initialize() {
        chatArea.appendText("Assistant IA SkillQuest: Bonjour ! Posez-moi n'importe quelle question sur Java.\n\n");
    }

    @FXML private void handleSend() {
        String msg = txtInput.getText();
        if (msg == null || msg.isBlank()) return;
        chatArea.appendText("Moi: " + msg + "\n");
        txtInput.clear();
        new Thread(() -> {
            try { Thread.sleep(400); } catch (Exception ignored) {}
            Platform.runLater(() -> {
                chatArea.appendText("\nAssistant IA: " + getResponse(msg) + "\n\n");
                chatArea.setScrollTop(Double.MAX_VALUE);
            });
        }).start();
    }

    private String getResponse(String msg) {
        String l = msg.toLowerCase();
        if (l.contains("bonjour") || l.contains("salut")) return "Bonjour ! Ravi de vous aider.";
        if (l.contains("héritage") || l.contains("heritage")) return "L'héritage (extends) permet à une classe d'hériter des méthodes et attributs d'une autre.";
        if (l.contains("interface")) return "Une interface définit un contrat (méthodes abstraites). Une classe peut implémenter plusieurs interfaces.";
        if (l.contains("exception")) return "En Java, les exceptions sont gérées avec try-catch-finally. Utilisez throws pour propager.";
        if (l.contains("collection")) return "Java Collections: List (ArrayList), Set (HashSet), Map (HashMap). Chacune a ses avantages.";
        if (l.contains("thread")) return "Un Thread est une unité d'exécution. En JavaFX, utilisez Platform.runLater() pour modifier l'UI depuis un thread.";
        return "Concept intéressant sur \"" + msg + "\". Voulez-vous un exemple de code ?";
    }
}
