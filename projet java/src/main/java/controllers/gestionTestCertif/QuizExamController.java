package controllers.gestionTestCertif;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.gestionTestCertif.Certification;
import models.gestionTestCertif.Exam;
import models.gestionTestCertif.ExamQuestion;
import services.gestionTestCertif.ServiceCertification;
import services.gestionTestCertif.ServiceExamQuestion;
import utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizExamController {

    @FXML private Label lbTimer, lbProgress, lbQuestion;
    @FXML private ProgressBar pbProgress;
    @FXML private VBox vbOptions;
    @FXML private Button btnNext, btnFinish;

    private List<ExamQuestion> questions;
    private int currentQuestionIndex = 0;
    private int totalPoints = 0, userScore = 0;
    private Exam currentExam;
    private int timeLeft;
    private Timeline timeline;
    private ToggleGroup toggleGroup;

    public void setExam(Exam exam) {
        this.currentExam = exam;
        this.timeLeft = exam.getDureeMinutes() * 60;
        loadQuestions(); startTimer(); showQuestion();
    }

    private void loadQuestions() {
        questions = new ServiceExamQuestion().getByExam(currentExam.getId());
        if (questions.isEmpty()) {
            int level = currentExam.getLevel();
            if (level <= 2) {
                questions.add(new ExamQuestion("Type pour entier ?", Arrays.asList("float", "int", "String", "boolean"), 1, 25));
                questions.add(new ExamQuestion("Commentaire une ligne ?", Arrays.asList("# comment", "// comment", "/* comment", "-- comment"), 1, 25));
                questions.add(new ExamQuestion("Boucle nombre connu ?", Arrays.asList("while", "if", "for", "switch"), 2, 25));
                questions.add(new ExamQuestion("Point d'entrée Java ?", Arrays.asList("start()", "main()", "run()", "init()"), 1, 25));
            } else if (level <= 5) {
                questions.add(new ExamQuestion("Héritage mot-clé ?", Arrays.asList("implements", "extends", "inherits", "super"), 1, 25));
                questions.add(new ExamQuestion("Classe non héritable ?", Arrays.asList("static", "final", "abstract", "private"), 1, 25));
                questions.add(new ExamQuestion("Collection clé-valeur ?", Arrays.asList("List", "Set", "Map", "Queue"), 2, 25));
                questions.add(new ExamQuestion("Constructeur ?", Arrays.asList("Détruire objet", "Initialiser objet", "Variable statique", "Interface"), 1, 25));
            } else {
                questions.add(new ExamQuestion("Classe mère toutes classes ?", Arrays.asList("System", "Main", "Object", "Root"), 2, 25));
                questions.add(new ExamQuestion("Gestion exceptions ?", Arrays.asList("try-catch", "if-else", "throw-throws", "A et C"), 3, 25));
                questions.add(new ExamQuestion("Interface fonctionnelle ?", Arrays.asList("Plusieurs méthodes", "Une méthode abstraite", "Classe abstraite", "Méthode statique"), 1, 25));
                questions.add(new ExamQuestion("Synchronisation threads ?", Arrays.asList("volatile", "transient", "synchronized", "atomic"), 2, 25));
            }
        }
        totalPoints = questions.stream().mapToInt(ExamQuestion::getPoints).sum();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            lbTimer.setText(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
            if (timeLeft <= 0) { timeline.stop(); handleFinish(null); }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); timeline.play();
    }

    private void showQuestion() {
        if (currentQuestionIndex >= questions.size()) { btnNext.setVisible(false); btnFinish.setVisible(true); return; }
        ExamQuestion q = questions.get(currentQuestionIndex);
        lbQuestion.setText(q.getText());
        lbProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
        pbProgress.setProgress((double)(currentQuestionIndex + 1) / questions.size());
        vbOptions.getChildren().clear();
        toggleGroup = new ToggleGroup();
        for (int i = 0; i < q.getOptions().size(); i++) {
            RadioButton rb = new RadioButton(q.getOptions().get(i));
            rb.setToggleGroup(toggleGroup); rb.setStyle("-fx-text-fill: white; -fx-font-size: 16;"); rb.setUserData(i);
            vbOptions.getChildren().add(rb);
        }
        if (currentQuestionIndex == questions.size() - 1) { btnNext.setVisible(false); btnFinish.setVisible(true); }
    }

    @FXML private void handleNext(ActionEvent event) { checkAnswer(); currentQuestionIndex++; showQuestion(); }

    private void checkAnswer() {
        RadioButton sel = (RadioButton) toggleGroup.getSelectedToggle();
        if (sel != null && (int) sel.getUserData() == questions.get(currentQuestionIndex).getCorrectOptionIndex())
            userScore += questions.get(currentQuestionIndex).getPoints();
    }

    @FXML private void handleFinish(ActionEvent event) {
        if (timeline != null) timeline.stop();
        checkAnswer();
        double pct = ((double) userScore / totalPoints) * 100;
        if (pct == 100) {
            boolean unlocked = SessionManager.getInstance().completeExam(currentExam);
            if (unlocked) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LevelUnlocked.fxml"));
                    Parent root = loader.load();
                    loader.<LevelUnlockedController>getController().setMessage(
                        "QUÊTE ACCOMPLIE ! Niveau " + currentExam.getLevel() + " maîtrisé.\nNiveau " + (currentExam.getLevel() + 1) + " débloqué !");
                    Stage popup = new Stage();
                    popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);
                    Scene sc = new Scene(root); sc.setFill(javafx.scene.paint.Color.TRANSPARENT);
                    popup.setScene(sc); popup.showAndWait();
                    afficherCertificat(event);
                } catch (IOException e) { e.printStackTrace(); }
            } else {
                try {
                    Stage popup = new Stage();
                    popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);
                    Scene sc = new Scene(FXMLLoader.load(getClass().getResource("/fxml/ExamSuccess.fxml")));
                    sc.setFill(javafx.scene.paint.Color.TRANSPARENT);
                    popup.setScene(sc); popup.showAndWait();
                    retourMenu(event);
                } catch (IOException e) { e.printStackTrace(); }
            }
        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Résultat"); a.setHeaderText("Score : " + (int) pct + "%");
            a.setContentText("100% requis. Réessayez !"); a.showAndWait(); retourMenu(null);
        }
    }

    private void afficherCertificat(ActionEvent event) {
        try {
            Certification cert = new ServiceCertification().getAll().stream()
                .filter(c -> c.getLevel() == currentExam.getLevel()).findFirst().orElseGet(() -> {
                    Certification def = new Certification(); def.setTitle("Certification " + currentExam.getNom());
                    def.setDescription("Réussi avec 100%."); def.setLevel(currentExam.getLevel()); return def;
                });
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CertificatVue.fxml"));
            Parent root = loader.load();
            loader.<CertificatVueController>getController().setData("Etudiant", cert);
            Stage stage = event != null ? (Stage)((Node)event.getSource()).getScene().getWindow() : (Stage) lbQuestion.getScene().getWindow();
            stage.setScene(new Scene(root)); stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void retourMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListeExamensNiveau.fxml"));
            Parent root = loader.load();
            loader.<ListeExamensNiveauController>getController().setNiveau(currentExam.getLevel());
            ((Stage) lbQuestion.getScene().getWindow()).setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}
