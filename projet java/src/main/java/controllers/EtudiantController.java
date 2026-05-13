package controllers;

import controllers.gestionJeux.GalaxyDefenderController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import models.Etudiant;
import models.ProgressionCours;
import models.Test;
import services.ServiceBadge;
import services.ServiceEtudiant;
import services.ServiceProgression;
import services.ServiceTest;
import utils.SessionManager;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur de l'espace Étudiant.
 */
public class EtudiantController implements Initializable {

    @FXML private Label nomComplet, emailEtudiant, labelNiveau;
    @FXML private Label pointsLabel, coursLabel, badgesCountLabel;
    @FXML private Label avatarInitial, telephoneLabel, sexeLabel;
    @FXML private ProgressBar levelProgress;
    @FXML private FlowPane badgesPane;
    @FXML private StackPane avatarStack;
    @FXML private ImageView photoProfilView;
    @FXML private Button btnProfil, btnCours, btnTests, btnClassement, btnJeux, btnExamens;

    // Sections
    @FXML private VBox sectionProfil, sectionCours, sectionTests, sectionClassement, sectionJeux, sectionExamens;

    // Tableaux
    @FXML private TableView<ProgressionCours> tableCoursProgression;
    @FXML private TableColumn<ProgressionCours, String>  colCoursTitre;
    @FXML private TableColumn<ProgressionCours, Integer> colCoursProgression;
    @FXML private TableColumn<ProgressionCours, String>  colCoursStatut;
    @FXML private TableColumn<ProgressionCours, Void>    colCoursActions;

    @FXML private TableView<Test> tableTestsDispo;
    @FXML private TableColumn<Test, String>  colTestTitre;
    @FXML private TableColumn<Test, Integer> colTestScoreMin;
    @FXML private TableColumn<Test, Void>    colTestActions;

    @FXML private TableView<Etudiant> tableClassement;
    @FXML private TableColumn<Etudiant, Integer> colRang;
    @FXML private TableColumn<Etudiant, String>  colLeaderNom;
    @FXML private TableColumn<Etudiant, Integer> colLeaderPoints;
    @FXML private TableColumn<Etudiant, Integer> colLeaderNiveau;

    private final ServiceEtudiant    serviceEtudiant    = new ServiceEtudiant();
    private final ServiceProgression serviceProgression = new ServiceProgression();
    private final ServiceBadge       serviceBadge       = new ServiceBadge();
    private final ServiceTest        serviceTest        = new ServiceTest();

    private Etudiant etudiant;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.etudiant = Session.getEtudiantConnecte();
        if (etudiant == null) return;

        chargerDonneesProfil();
        configurerTables();
        afficherProfil(null);
    }

    // =====================================================================
    // PROFIL
    // =====================================================================
    private void chargerDonneesProfil() {
        // Recharger depuis la BDD pour avoir les données à jour
        Etudiant frais = serviceEtudiant.getById(etudiant.getId());
        if (frais != null) {
            etudiant = frais;
            Session.setEtudiantConnecte(etudiant);
        }

        nomComplet.setText(etudiant.getPrenom() + " " + etudiant.getNom());
        emailEtudiant.setText(etudiant.getEmail());
        avatarInitial.setText(etudiant.getNom().substring(0, 1).toUpperCase());
        pointsLabel.setText(String.valueOf(etudiant.getPoints()));

        // Téléphone / Sexe
        String tel = etudiant.getTelephone();
        telephoneLabel.setText("📞  " + (tel != null && !tel.isEmpty() ? tel : "—"));
        String sexe = etudiant.getSexe();
        sexeLabel.setText("👤  " + ("F".equals(sexe) ? "Féminin" : "M".equals(sexe) ? "Masculin" : "—"));

        // Calcul Niveau & Progrès
        int lvl = (etudiant.getPoints() / 1000) + 1;
        double progress = (etudiant.getPoints() % 1000) / 1000.0;
        labelNiveau.setText("Niveau " + lvl);
        levelProgress.setProgress(progress);

        // Stats
        int nbCours = serviceProgression.getProgressionByEtudiant(etudiant.getId()).size();
        coursLabel.setText(String.valueOf(nbCours));

        // Badges
        var badges = serviceBadge.getBadgesEtudiant(etudiant.getId());
        badgesCountLabel.setText(String.valueOf(badges.size()));
        badgesPane.getChildren().clear();
        if (badges.isEmpty()) {
            Label vide = new Label("Aucun badge encore — continuez à progresser !");
            vide.setStyle("-fx-text-fill:#a8a8b3; -fx-font-size:12px;");
            badgesPane.getChildren().add(vide);
        } else {
            badges.forEach(b -> {
                Label badgeLabel = new Label("🏅 " + b.getNom());
                badgeLabel.setStyle("-fx-background-color:#0f3460; -fx-text-fill:#f39c12; " +
                        "-fx-padding:5 12; -fx-background-radius:15; -fx-font-size:12px;");
                badgesPane.getChildren().add(badgeLabel);
            });
        }

        // Photo de profil
        chargerPhotoProfil(etudiant.getPhotoProfil());
    }

    private void chargerPhotoProfil(String chemin) {
        if (chemin != null && !chemin.isEmpty()) {
            File f = new File(chemin);
            if (f.exists()) {
                try {
                    Image img = new Image(f.toURI().toString());
                    photoProfilView.setImage(img);
                    // Clip circulaire
                    Circle clip = new Circle(45, 45, 45);
                    photoProfilView.setClip(clip);
                    photoProfilView.setVisible(true);
                    photoProfilView.setManaged(true);
                    avatarStack.setVisible(false);
                    avatarStack.setManaged(false);
                    return;
                } catch (Exception ignored) {}
            }
        }
        // Pas de photo → initiale
        photoProfilView.setVisible(false);
        photoProfilView.setManaged(false);
        avatarStack.setVisible(true);
        avatarStack.setManaged(true);
    }

    @FXML
    private void ouvrirModifierProfil(ActionEvent e) {
        App.ouvrirFenetreModal("ModifierProfil", "Modifier mon Profil");
        // Rafraîchir après modification
        this.etudiant = Session.getEtudiantConnecte();
        chargerDonneesProfil();
    }

    // =====================================================================
    // NAVIGATION
    // =====================================================================
    @FXML
    private void afficherProfil(ActionEvent e) {
        setSectionsVisibility(sectionProfil);
        updateSidebarStyles(btnProfil);
    }

    @FXML
    private void ouvrirFormInscription(ActionEvent e) {
        App.ouvrirFenetreModal("InscrireCours", "Inscription à un cours");
        afficherCours(null);
    }

    @FXML
    private void afficherCours(ActionEvent e) {
        setSectionsVisibility(sectionCours);
        updateSidebarStyles(btnCours);
        tableCoursProgression.setItems(
                FXCollections.observableArrayList(serviceProgression.getProgressionByEtudiant(etudiant.getId())));
    }

    @FXML
    private void afficherTests(ActionEvent e) {
        setSectionsVisibility(sectionTests);
        updateSidebarStyles(btnTests);
        tableTestsDispo.setItems(FXCollections.observableArrayList(serviceTest.getAll()));
    }

    @FXML
    private void afficherClassement(ActionEvent e) {
        setSectionsVisibility(sectionClassement);
        updateSidebarStyles(btnClassement);
        tableClassement.setItems(FXCollections.observableArrayList(serviceEtudiant.getClassement()));
    }

    @FXML
    private void afficherJeux(ActionEvent e) {
        setSectionsVisibility(sectionJeux);
        updateSidebarStyles(btnJeux);
    }

    @FXML
    private void afficherExamens(ActionEvent e) {
        setSectionsVisibility(sectionExamens);
        updateSidebarStyles(btnExamens);
    }

    @FXML
    private void lancerQuiz(ActionEvent e) {
        App.ouvrirFenetreModal("QuizGame", "Quiz Java");
    }

    @FXML
    private void lancerCorrection(ActionEvent e) {
        App.ouvrirFenetreModal("CodeCorrection", "Correction de Code");
    }

    @FXML
    private void lancerBattle(ActionEvent e) {
        App.ouvrirFenetreModal("BattleGame", "Battle Arena");
    }

    @FXML
    private void lancerGalaxy(ActionEvent e) {
        GalaxyDefenderController.launch(App.getPrimaryStage());
    }

    @FXML
    private void lancerExamens(ActionEvent e) {
        SessionManager.getInstance().setAdmin(false);
        App.ouvrirFenetreModal("SelectionNiveau", "Examens & Certifications");
    }

    // =====================================================================
    // TABLES
    // =====================================================================
    private void configurerTables() {
        // Table Cours
        colCoursTitre.setCellValueFactory(new PropertyValueFactory<>("titreCours"));
        colCoursStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colCoursProgression.setCellFactory(column -> new TableCell<>() {
            private final ProgressBar pb = new ProgressBar();
            { pb.setPrefWidth(100); pb.setStyle("-fx-accent: #27ae60;"); }
            @Override protected void updateItem(Integer progress, boolean empty) {
                super.updateItem(progress, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) { setGraphic(null); return; }
                ProgressionCours p = getTableView().getItems().get(getIndex());
                pb.setProgress(p.getPourcentage() / 100.0);
                setGraphic(pb);
            }
        });

        colCoursActions.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Continuer ▶");
            {
                btn.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-background-radius:5; -fx-cursor:hand;");
                btn.setOnAction(ev -> System.out.println("Lancer cours..."));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Table Tests
        colTestTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colTestScoreMin.setCellValueFactory(new PropertyValueFactory<>("scoreMin"));
        colTestActions.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Lancer ▶");
            {
                btn.setStyle("-fx-background-color:#27ae60; -fx-text-fill:white; -fx-background-radius:5; -fx-cursor:hand;");
                btn.setOnAction(ev -> {
                    Test selectedTest = getTableView().getItems().get(getIndex());
                    lancerTest(selectedTest);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        // Table Classement
        configurerTableClassement();
    }

    private void lancerTest(Test test) {
        System.out.println("Lancement du test : " + test.getTitre());
        // Ici on passe le test au controller de l'interface "PasserTest"
        // FXMLLoader loader = App.ouvrirFenetreModalAvecLoader("PasserTest", "Passer le Test : " + test.getTitre());
        // PasserTestController controller = loader.getController();
        // controller.setTest(test);
        // loader.getController().show();
    }     
    
    // Table Classement
    private void configurerTableClassement() {
        colRang.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                int rang = getIndex() + 1;
                setText(rang == 1 ? "🥇 1" : rang == 2 ? "🥈 2" : rang == 3 ? "🥉 3" : String.valueOf(rang));
            }
        });
        colLeaderNom.setCellValueFactory(e ->
                new SimpleStringProperty(e.getValue().getPrenom() + " " + e.getValue().getNom()));
        colLeaderPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colLeaderNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
    }

    // =====================================================================
    // UTILITAIRES
    // =====================================================================
    private void setSectionsVisibility(VBox activeSection) {
        VBox[] sections = {sectionProfil, sectionCours, sectionTests, sectionClassement, sectionJeux, sectionExamens};
        for (VBox s : sections) {
            if (s == null) continue;
            s.setVisible(s == activeSection);
            s.setManaged(s == activeSection);
        }
    }

    private void updateSidebarStyles(Button activeBtn) {
        Button[] buttons = {btnProfil, btnCours, btnTests, btnClassement, btnJeux, btnExamens};
        for (Button b : buttons) {
            if (b == null) continue;
            if (b == activeBtn) {
                b.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-alignment:CENTER_LEFT; -fx-padding:12 20; -fx-background-radius:8;");
            } else {
                b.setStyle("-fx-background-color:transparent; -fx-text-fill:#a8a8b3; -fx-alignment:CENTER_LEFT; -fx-padding:12 20;");
            }
        }
    }

    @FXML
    private void handleDeconnexion(ActionEvent e) {
        Session.setEtudiantConnecte(null);
        App.ouvrirScene("Login");
    }
}
