package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import models.*;
import services.*;
import utils.SessionManager;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Contrôleur du tableau de bord Admin.
 */
public class AdminController implements Initializable {

    // ── Sidebar ──────────────────────────────────────────────────────
    @FXML private Button btnEtudiants;
    @FXML private Button btnCours;
    @FXML private Button btnTests;
    @FXML private Button btnCertificats;
    @FXML private Button btnJeux;
    @FXML private Button btnExamsCertifs;
    @FXML private Label  titreSection;

    // ── Barre de recherche et Filtres ────────────────────────────────
    @FXML private TextField rechercheField;
    @FXML private ComboBox<String> filtreCombo;
    @FXML private Button    btnAjouter;

    // ── Tableaux ─────────────────────────────────────────────────────
    @FXML private TableView<Etudiant>       tableEtudiants;
    @FXML private TableColumn<Etudiant,String>  colNom;
    @FXML private TableColumn<Etudiant,String>  colPrenom;
    @FXML private TableColumn<Etudiant,String>  colEmail;
    @FXML private TableColumn<Etudiant,String>  colTelephone;
    @FXML private TableColumn<Etudiant,String>  colSexe;
    @FXML private TableColumn<Etudiant,Integer> colNiveau;
    @FXML private TableColumn<Etudiant,Boolean> colBloque;
    @FXML private TableColumn<Etudiant,Void>    colActions;

    @FXML private TableView<Cours>       tableCours;
    @FXML private TableColumn<Cours,String>  colCoursTitre;
    @FXML private TableColumn<Cours,Integer> colCoursNiveau;
    @FXML private TableColumn<Cours,Integer> colCoursAdmin;
    @FXML private TableColumn<Cours,Void>    colCoursActions;

    @FXML private TableView<Test>        tableTests;
    @FXML private TableColumn<Test,String>   colTestTitre;
    @FXML private TableColumn<Test,Integer>  colTestScore;
    @FXML private TableColumn<Test,Integer>  colTestCours;
    @FXML private TableColumn<Test,Void>     colTestActions;

    @FXML private TableView<Certificat>   tableCertificats;
    @FXML private TableColumn<Certificat,String>  colCertifNom;
    @FXML private TableColumn<Certificat,Integer> colCertifTest;
    @FXML private TableColumn<Certificat,Void>    colCertifActions;

    @FXML private TableView<Jeu>         tableJeux;
    @FXML private TableColumn<Jeu,String>  colJeuNom;
    @FXML private TableColumn<Jeu,String>  colJeuType;
    @FXML private TableColumn<Jeu,Void>    colJeuActions;

    private final ServiceEtudiant   serviceEtudiant   = new ServiceEtudiant();
    private final ServiceCours      serviceCours      = new ServiceCours();
    private final ServiceTest       serviceTest       = new ServiceTest();
    private final ServiceCertificat serviceCertificat = new ServiceCertificat();
    private final ServiceJeu        serviceJeu        = new ServiceJeu();

    private ObservableList<Etudiant>   listeEtudiants   = FXCollections.observableArrayList();
    private ObservableList<Cours>      listeCours       = FXCollections.observableArrayList();
    private ObservableList<Test>       listeTests       = FXCollections.observableArrayList();
    private ObservableList<Certificat> listeCertificats = FXCollections.observableArrayList();
    private ObservableList<Jeu>        listeJeux        = FXCollections.observableArrayList();

    private String currentMode = "ETUDIANTS";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurerColonnesEtudiants();
        configurerColonnesCours();
        configurerColonnesTests();
        configurerColonnesCertificats();
        configurerColonnesJeux();
        
        configurerFiltres("ETUDIANTS");
        chargerEtudiants();
    }

    private void configurerFiltres(String mode) {
        filtreCombo.getItems().clear();
        switch (mode) {
            case "ETUDIANTS":
                filtreCombo.getItems().addAll("Tout", "Nom", "Prénom", "Email", "Téléphone", "Niveau");
                break;
            case "COURS":
                filtreCombo.getItems().addAll("Tout", "Titre", "Niveau requis");
                break;
            case "TESTS":
                filtreCombo.getItems().addAll("Tout", "Titre", "Score Min");
                break;
            case "CERTIFICATS":
                filtreCombo.getItems().addAll("Tout", "Nom");
                break;
            case "JEUX":
                filtreCombo.getItems().addAll("Tout", "Nom", "Type");
                break;
        }
        filtreCombo.getSelectionModel().selectFirst();
    }

    // ================================================================
    // NAVIGATION
    // ================================================================
    @FXML
    private void afficherEtudiants(ActionEvent e) {
        currentMode = "ETUDIANTS";
        titreSection.setText("Gestion des Étudiants");
        configurerFiltres(currentMode);
        setTablesVisibility(tableEtudiants);
        updateSidebarStyles(btnEtudiants);
        chargerEtudiants();
    }

    @FXML
    private void afficherCours(ActionEvent e) {
        currentMode = "COURS";
        titreSection.setText("Gestion des Cours");
        configurerFiltres(currentMode);
        setTablesVisibility(tableCours);
        updateSidebarStyles(btnCours);
        chargerCours();
    }

    @FXML
    private void afficherTests(ActionEvent e) {
        currentMode = "TESTS";
        titreSection.setText("Gestion des Tests");
        configurerFiltres(currentMode);
        setTablesVisibility(tableTests);
        updateSidebarStyles(btnTests);
        chargerTests();
    }

    @FXML
    private void afficherCertificats(ActionEvent e) {
        currentMode = "CERTIFICATS";
        titreSection.setText("Gestion des Certificats");
        configurerFiltres(currentMode);
        setTablesVisibility(tableCertificats);
        updateSidebarStyles(btnCertificats);
        chargerCertificats();
    }

    @FXML
    private void afficherJeux(ActionEvent e) {
        currentMode = "JEUX";
        titreSection.setText("Gestion des Jeux");
        configurerFiltres(currentMode);
        setTablesVisibility(tableJeux);
        updateSidebarStyles(btnJeux);
        chargerJeux();
    }

    @FXML
    private void ouvrirGestionJeux(ActionEvent e) {
        App.ouvrirFenetreModal("AdminGames", "Gestion des Jeux (Avancé)");
    }

    @FXML
    private void ouvrirGestionExamsCertifs(ActionEvent e) {
        SessionManager.getInstance().setAdmin(true);
        App.ouvrirFenetreModal("SelectionNiveau", "Gestion Examens & Certifications");
    }

    private void setTablesVisibility(TableView<?> activeTable) {
        tableEtudiants.setVisible(activeTable == tableEtudiants);
        tableEtudiants.setManaged(activeTable == tableEtudiants);
        tableCours.setVisible(activeTable == tableCours);
        tableCours.setManaged(activeTable == tableCours);
        tableTests.setVisible(activeTable == tableTests);
        tableTests.setManaged(activeTable == tableTests);
        tableCertificats.setVisible(activeTable == tableCertificats);
        tableCertificats.setManaged(activeTable == tableCertificats);
        tableJeux.setVisible(activeTable == tableJeux);
        tableJeux.setManaged(activeTable == tableJeux);
    }

    private void updateSidebarStyles(Button activeButton) {
        Button[] buttons = {btnEtudiants, btnCours, btnTests, btnCertificats, btnJeux, btnExamsCertifs};
        for (Button b : buttons) {
            if (b == activeButton) {
                b.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-font-size:13px; -fx-background-radius:6; -fx-padding:10; -fx-cursor:hand; -fx-alignment:CENTER-LEFT;");
            } else {
                b.setStyle("-fx-background-color:transparent; -fx-text-fill:#a8a8b3; -fx-font-size:13px; -fx-background-radius:6; -fx-padding:10; -fx-cursor:hand; -fx-alignment:CENTER-LEFT;");
            }
        }
    }

    @FXML
    private void handleDeconnexion(ActionEvent e) {
        Session.vider();
        App.ouvrirScene("Login");
    }

    // ================================================================
    // RECHERCHE
    // ================================================================
    @FXML
    private void handleRecherche() {
        String filtreTxt = rechercheField.getText().trim().toLowerCase();
        String categorie = filtreCombo.getSelectionModel().getSelectedItem();
        if (categorie == null) categorie = "Tout";

        switch (currentMode) {
            case "ETUDIANTS":
                final String finalCatE = categorie;
                tableEtudiants.setItems(listeEtudiants.filtered(et -> {
                    if (filtreTxt.isEmpty()) return true;
                    return switch (finalCatE) {
                        case "Nom" -> et.getNom().toLowerCase().contains(filtreTxt);
                        case "Prénom" -> et.getPrenom().toLowerCase().contains(filtreTxt);
                        case "Email" -> et.getEmail().toLowerCase().contains(filtreTxt);
                        case "Téléphone" -> et.getTelephone() != null && et.getTelephone().contains(filtreTxt);
                        case "Niveau" -> String.valueOf(et.getNiveau()).contains(filtreTxt);
                        default -> et.getNom().toLowerCase().contains(filtreTxt) || et.getPrenom().toLowerCase().contains(filtreTxt) || et.getEmail().toLowerCase().contains(filtreTxt);
                    };
                }));
                break;
            case "COURS":
                final String finalCatC = categorie;
                tableCours.setItems(listeCours.filtered(c -> {
                    if (filtreTxt.isEmpty()) return true;
                    return switch (finalCatC) {
                        case "Titre" -> c.getTitre().toLowerCase().contains(filtreTxt);
                        case "Niveau requis" -> String.valueOf(c.getNiveauRequis()).contains(filtreTxt);
                        default -> c.getTitre().toLowerCase().contains(filtreTxt);
                    };
                }));
                break;
            case "TESTS":
                final String finalCatT = categorie;
                tableTests.setItems(listeTests.filtered(t -> {
                    if (filtreTxt.isEmpty()) return true;
                    return switch (finalCatT) {
                        case "Titre" -> t.getTitre().toLowerCase().contains(filtreTxt);
                        case "Score Min" -> String.valueOf(t.getScoreMin()).contains(filtreTxt);
                        default -> t.getTitre().toLowerCase().contains(filtreTxt);
                    };
                }));
                break;
            case "CERTIFICATS":
                tableCertificats.setItems(listeCertificats.filtered(c -> c.getNom().toLowerCase().contains(filtreTxt)));
                break;
            case "JEUX":
                final String finalCatJ = categorie;
                tableJeux.setItems(listeJeux.filtered(j -> {
                    if (filtreTxt.isEmpty()) return true;
                    return switch (finalCatJ) {
                        case "Nom" -> j.getNom().toLowerCase().contains(filtreTxt);
                        case "Type" -> j.getType().toLowerCase().contains(filtreTxt);
                        default -> j.getNom().toLowerCase().contains(filtreTxt) || j.getType().toLowerCase().contains(filtreTxt);
                    };
                }));
                break;
        }
    }

    // ================================================================
    // CONFIGURATION DES TABLES
    // ================================================================
    private void configurerColonnesEtudiants() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTelephone()));
        colSexe.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getSexe()));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        colBloque.setCellValueFactory(new PropertyValueFactory<>("estBloque"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnModif = new Button("✏");
            private final Button btnBloc = new Button("🔒");
            private final Button btnSuppr = new Button("🗑");
            private final HBox boite = new HBox(5, btnModif, btnBloc, btnSuppr);
            {
                String base = "-fx-background-radius:5; -fx-cursor:hand; -fx-padding:4 8 4 8;";
                btnModif.setStyle(base + "-fx-background-color:#0f3460; -fx-text-fill:white;");
                btnBloc.setStyle(base + "-fx-background-color:#e94560; -fx-text-fill:white;");
                btnSuppr.setStyle(base + "-fx-background-color:#c0392b; -fx-text-fill:white;");
                btnModif.setOnAction(ev -> ouvrirFormModifEtudiant(getTableView().getItems().get(getIndex())));
                btnBloc.setOnAction(ev -> {
                    Etudiant et = getTableView().getItems().get(getIndex());
                    if (et.isEstBloque()) serviceEtudiant.debloquer(et.getId());
                    else serviceEtudiant.bloquer(et.getId());
                    chargerEtudiants();
                });
                btnSuppr.setOnAction(ev -> {
                    Etudiant et = getTableView().getItems().get(getIndex());
                    if (confirmerSuppression(et.getNom())) { serviceEtudiant.delete(et); chargerEtudiants(); }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    btnBloc.setText(getTableView().getItems().get(getIndex()).isEstBloque() ? "🔓" : "🔒");
                    setGraphic(boite);
                }
            }
        });
        tableEtudiants.setItems(listeEtudiants);
    }

    private void configurerColonnesCours() {
        colCoursTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCoursNiveau.setCellValueFactory(new PropertyValueFactory<>("niveauRequis"));
        colCoursAdmin.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colCoursActions.setCellFactory(col -> createActionCell(this::ouvrirFormModifCours, c -> { serviceCours.delete(c); chargerCours(); }));
        tableCours.setItems(listeCours);
    }

    private void configurerColonnesTests() {
        colTestTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colTestScore.setCellValueFactory(new PropertyValueFactory<>("scoreMin"));
        colTestCours.setCellValueFactory(new PropertyValueFactory<>("coursId"));
        colTestActions.setCellFactory(col -> createActionCell(this::ouvrirFormModifTest, t -> { serviceTest.delete(t); chargerTests(); }));
        tableTests.setItems(listeTests);
    }

    private void configurerColonnesCertificats() {
        colCertifNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCertifTest.setCellValueFactory(new PropertyValueFactory<>("testId"));
        colCertifActions.setCellFactory(col -> createActionCell(this::ouvrirFormModifCertificat, c -> { serviceCertificat.delete(c); chargerCertificats(); }));
        tableCertificats.setItems(listeCertificats);
    }

    private void configurerColonnesJeux() {
        colJeuNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colJeuType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colJeuActions.setCellFactory(col -> createActionCell(this::ouvrirFormModifJeu, j -> { serviceJeu.delete(j); chargerJeux(); }));
        tableJeux.setItems(listeJeux);
    }

    private <T> TableCell<T, Void> createActionCell(java.util.function.Consumer<T> editAction, java.util.function.Consumer<T> deleteAction) {
        return new TableCell<>() {
            private final Button btnModif = new Button("✏");
            private final Button btnSuppr = new Button("🗑");
            private final HBox boite = new HBox(5, btnModif, btnSuppr);
            {
                String base = "-fx-background-radius:5; -fx-cursor:hand; -fx-padding:4 8 4 8;";
                btnModif.setStyle(base + "-fx-background-color:#0f3460; -fx-text-fill:white;");
                btnSuppr.setStyle(base + "-fx-background-color:#c0392b; -fx-text-fill:white;");
                btnModif.setOnAction(ev -> editAction.accept(getTableView().getItems().get(getIndex())));
                btnSuppr.setOnAction(ev -> {
                    if (confirmerSuppression("cet élément")) deleteAction.accept(getTableView().getItems().get(getIndex()));
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : boite);
            }
        };
    }

    // ================================================================
    // ACTIONS & CHARGEMENT
    // ================================================================
    private void chargerEtudiants() { listeEtudiants.setAll(serviceEtudiant.getAll()); }
    private void chargerCours()     { listeCours.setAll(serviceCours.getAll()); }
    private void chargerTests()     { listeTests.setAll(serviceTest.getAll()); }
    private void chargerCertificats() { listeCertificats.setAll(serviceCertificat.getAll()); }
    private void chargerJeux()      { listeJeux.setAll(serviceJeu.getAll()); }

    @FXML
    private void handleAjouter(ActionEvent e) {
        switch (currentMode) {
            case "ETUDIANTS": App.ouvrirFenetreModal("AjouterEtudiant", "Ajouter Étudiant"); chargerEtudiants(); break;
            case "COURS":
                FXMLLoader l = App.ouvrirFenetreModalAvecLoader("AjouterCours", "Ajouter Cours");
                if (l != null) { ((AjouterCoursController)l.getController()).setAdminId(Session.getAdminConnecte().getId()); App.getStageFromLoader(l).showAndWait(); chargerCours(); }
                break;
            case "TESTS": App.ouvrirFenetreModal("AjouterTest", "Ajouter Test"); chargerTests(); break;
            case "CERTIFICATS": App.ouvrirFenetreModal("AjouterCertificat", "Ajouter Certificat"); chargerCertificats(); break;
            case "JEUX": App.ouvrirFenetreModal("AjouterJeu", "Ajouter Jeu"); chargerJeux(); break;
        }
    }

    private void ouvrirFormModifEtudiant(Etudiant et) { FXMLLoader l = App.ouvrirFenetreModalAvecLoader("AjouterEtudiant", "Modifier Étudiant"); if (l!=null) { ((AjouterEtudiantController)l.getController()).chargerEtudiant(et); App.getStageFromLoader(l).showAndWait(); chargerEtudiants(); } }
    private void ouvrirFormModifCours(Cours c) { FXMLLoader l = App.ouvrirFenetreModalAvecLoader("AjouterCours", "Modifier Cours"); if (l!=null) { ((AjouterCoursController)l.getController()).chargerCours(c); App.getStageFromLoader(l).showAndWait(); chargerCours(); } }
    private void ouvrirFormModifTest(Test t) { FXMLLoader l = App.ouvrirFenetreModalAvecLoader("AjouterTest", "Modifier Test"); if (l!=null) { ((AjouterTestController)l.getController()).chargerTest(t); App.getStageFromLoader(l).showAndWait(); chargerTests(); } }
    private void ouvrirFormModifCertificat(Certificat c) { FXMLLoader l = App.ouvrirFenetreModalAvecLoader("AjouterCertificat", "Modifier Certificat"); if (l!=null) { ((AjouterCertificatController)l.getController()).chargerCertificat(c); App.getStageFromLoader(l).showAndWait(); chargerCertificats(); } }
    private void ouvrirFormModifJeu(Jeu j) { FXMLLoader l = App.ouvrirFenetreModalAvecLoader("AjouterJeu", "Modifier Jeu"); if (l!=null) { ((AjouterJeuController)l.getController()).chargerJeu(j); App.getStageFromLoader(l).showAndWait(); chargerJeux(); } }

    private boolean confirmerSuppression(String nom) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation");
        a.setHeaderText("Supprimer : " + nom);
        a.setContentText("Confirmer la suppression ?");
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }
}
