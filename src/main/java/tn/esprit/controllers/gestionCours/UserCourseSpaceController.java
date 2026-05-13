package tn.esprit.controllers.gestionCours;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.enties.Cours;
import tn.esprit.enties.Lecon;
import tn.esprit.services.CoursService;
import tn.esprit.services.LeconService;
import tn.esprit.utils.SessionManager;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserCourseSpaceController {

    @FXML
    private FlowPane courseGrid;

    @FXML
    private ComboBox<String> comboFieldFilter;

    @FXML
    private ComboBox<String> comboOwnerFilter;

    @FXML
    private ComboBox<String> comboLevel;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> comboSort;

    @FXML
    private Label lblStatus;

    private final CoursService coursService = new CoursService();
    private final LeconService leconService = new LeconService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<Cours> allCourses = new ArrayList<>();
    private List<Lecon> allLecons = new ArrayList<>();

    @FXML
    public void initialize() {
        comboFieldFilter.setItems(FXCollections.observableArrayList("Tous", "Titre", "Niveau", "Contenue", "Date d'ajout", "Id Ajouteur"));
        comboFieldFilter.setValue("Tous");

        comboOwnerFilter.setItems(FXCollections.observableArrayList("Tous", "Cours de l'app", "Cours de l'utilisateur"));
        comboOwnerFilter.setValue("Tous");

        comboLevel.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6"));
        comboLevel.setValue(String.valueOf(SessionManager.getCurrentUserLevel()));

        comboSort.setItems(FXCollections.observableArrayList("Aucun tri", "Niveau croissant", "Niveau decroissant", "Date recente", "Titre A-Z"));
        comboSort.setValue("Aucun tri");

        comboFieldFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderCourses());
        comboOwnerFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderCourses());
        comboLevel.valueProperty().addListener((obs, oldValue, newValue) -> {
            SessionManager.setCurrentUserLevel(Integer.parseInt(newValue));
            renderCourses();
        });
        comboSort.valueProperty().addListener((obs, oldValue, newValue) -> renderCourses());
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> renderCourses());

        loadData();
    }

    private void loadData() {
        try {
            allCourses = coursService.getAll();
            allLecons = leconService.getAll();
            renderCourses();
        } catch (SQLException e) {
            lblStatus.setText("Impossible de charger les cours : " + e.getMessage());
        }
    }

    private void renderCourses() {
        courseGrid.getChildren().clear();

        List<Cours> visibleCourses = new ArrayList<>();
        for (Cours cours : allCourses) {
            if (matchesFilters(cours)) {
                visibleCourses.add(cours);
            }
        }

        applySort(visibleCourses);

        for (Cours cours : visibleCourses) {
            courseGrid.getChildren().add(createCourseCard(cours));
        }

        lblStatus.setText(visibleCourses.size() + " cours affiches");
    }

    private boolean matchesFilters(Cours cours) {
        String ownerFilter = comboOwnerFilter.getValue();
        int currentUserId = SessionManager.getCurrentUserId();

        if ("Cours de l'utilisateur".equals(ownerFilter) && cours.getIdAjouteur() != currentUserId) {
            return false;
        }

        if ("Cours de l'app".equals(ownerFilter) && cours.getIdAjouteur() == currentUserId) {
            return false;
        }

        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            return true;
        }

        return matchesFieldFilter(cours, keyword);
    }

    private boolean matchesFieldFilter(Cours cours, String keyword) {
        String fieldFilter = comboFieldFilter.getValue();

        if ("Titre".equals(fieldFilter)) {
            return contains(cours.getTitre(), keyword);
        }

        if ("Niveau".equals(fieldFilter)) {
            return contains(cours.getNiveau(), keyword);
        }

        if ("Contenue".equals(fieldFilter)) {
            return contains(cours.getContenue(), keyword);
        }

        if ("Date d'ajout".equals(fieldFilter)) {
            return contains(formatDate(cours), keyword);
        }

        if ("Id Ajouteur".equals(fieldFilter)) {
            return contains(String.valueOf(cours.getIdAjouteur()), keyword);
        }

        return contains(cours.getTitre(), keyword)
                || contains(cours.getDescription(), keyword)
                || contains(cours.getContenue(), keyword)
                || contains(cours.getNiveau(), keyword)
                || contains(String.valueOf(cours.getIdAjouteur()), keyword)
                || contains(formatDate(cours), keyword);
    }

    private void applySort(List<Cours> courses) {
        String sort = comboSort.getValue();

        if ("Niveau croissant".equals(sort)) {
            courses.sort(Comparator.comparingInt(this::niveauAsInt));
        } else if ("Niveau decroissant".equals(sort)) {
            courses.sort(Comparator.comparingInt(this::niveauAsInt).reversed());
        } else if ("Date recente".equals(sort)) {
            courses.sort(Comparator.comparing(Cours::getDateDeCreation, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        } else if ("Titre A-Z".equals(sort)) {
            courses.sort(Comparator.comparing(cours -> valueOrEmpty(cours.getTitre()).toLowerCase()));
        }
    }

    private VBox createCourseCard(Cours cours) {
        boolean unlocked = niveauAsInt(cours) <= SessionManager.getCurrentUserLevel();

        VBox card = new VBox(12);
        card.getStyleClass().add(unlocked ? "user-course-card" : "user-course-card-locked");

        Label title = new Label(valueOrEmpty(cours.getTitre()));
        title.getStyleClass().add("user-card-title");
        title.setWrapText(true);

        Label level = new Label("niveau: " + valueOrEmpty(cours.getNiveau()));
        level.getStyleClass().add("user-card-level");

        Label description = new Label(valueOrEmpty(cours.getDescription()));
        description.getStyleClass().add("user-card-description");
        description.setWrapText(true);

        Label owner = new Label(cours.getIdAjouteur() == SessionManager.getCurrentUserId() ? "Mes cours" : "Cours de l'app");
        owner.getStyleClass().add("user-card-owner");

        Button resources = new Button(unlocked ? "Ressources" : "Niveau verrouille");
        resources.getStyleClass().add(unlocked ? "resource-button" : "locked-button");
        resources.setDisable(!unlocked);

        VBox details = new VBox(8);
        details.getStyleClass().add("resource-panel");
        details.setVisible(false);
        details.setManaged(false);
        fillResources(details, cours);

        resources.setOnAction(event -> {
            boolean show = !details.isVisible();
            details.setVisible(show);
            details.setManaged(show);
            resources.setText(show ? "Masquer ressources" : "Ressources");
        });

        card.getChildren().addAll(title, level, description, owner, resources, details);
        return card;
    }

    private void fillResources(VBox details, Cours cours) {
        details.getChildren().clear();
        details.getChildren().add(createContentBlock(cours));

        boolean hasLecons = false;
        for (Lecon lecon : allLecons) {
            if (lecon.getCours() != null && lecon.getCours().getId() == cours.getId()) {
                hasLecons = true;
                details.getChildren().add(createResourceRow("Lecon: " + lecon.getTitre(), valueOrDefault(lecon.getDescription(), "Aucune description")));
            }
        }

        if (!hasLecons) {
            details.getChildren().add(createResourceRow("Lecons", "Aucune lecon liee a ce cours"));
        }

        details.getChildren().add(createResourceRow("Tests", "Aucun test lie pour le moment"));
    }

    private HBox createResourceRow(String label, String value) {
        HBox row = new HBox(10);
        row.getStyleClass().add("resource-row-user");

        Label icon = new Label("▸");
        icon.getStyleClass().add("resource-row-icon");

        Label text = new Label(label + " : " + value);
        text.getStyleClass().add("resource-row-text");
        text.setWrapText(true);

        row.getChildren().addAll(icon, text);
        return row;
    }

    private VBox createContentBlock(Cours cours) {
        VBox block = new VBox(8);
        block.getStyleClass().add("content-reader");

        Label title = new Label("Contenu du cours");
        title.getStyleClass().add("resource-row-text");

        TextArea content = new TextArea(valueOrDefault(cours.getContenue(), "Aucun fichier ou lien"));
        content.getStyleClass().add("content-reader-area");
        content.setEditable(false);
        content.setWrapText(true);
        content.setPrefRowCount(4);

        Button openButton = new Button("Ouvrir le contenu");
        openButton.getStyleClass().add("resource-button");
        openButton.setDisable(cours.getContenue() == null || cours.getContenue().trim().isEmpty());
        openButton.setOnAction(event -> openCourseContent(cours.getContenue()));

        block.getChildren().addAll(title, content, openButton);
        return block;
    }

    @FXML
    private void uploadCourse() {
        try {
            SessionManager.setAddCourseReturnPath("/gestionCours/UserCourseSpace.fxml");
            Parent parent = FXMLLoader.load(getClass().getResource("/gestionCours/addCours.fxml"));
            courseGrid.getScene().setRoot(parent);
        } catch (IOException e) {
            lblStatus.setText("Impossible d'ouvrir la page d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void resetFilters() {
        comboFieldFilter.setValue("Tous");
        comboOwnerFilter.setValue("Tous");
        comboSort.setValue("Aucun tri");
        txtSearch.clear();
        renderCourses();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private int niveauAsInt(Cours cours) {
        try {
            return Integer.parseInt(valueOrEmpty(cours.getNiveau()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String formatDate(Cours cours) {
        return cours.getDateDeCreation() == null ? "" : dateFormat.format(cours.getDateDeCreation());
    }

    private void openCourseContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        try {
            if (content.startsWith("http://") || content.startsWith("https://")) {
                Desktop.getDesktop().browse(java.net.URI.create(content));
                return;
            }

            Path path = Paths.get(content);
            if (Files.exists(path)) {
                Desktop.getDesktop().open(path.toFile());
            } else {
                lblStatus.setText("Fichier introuvable : " + content);
            }
        } catch (IOException | IllegalArgumentException e) {
            lblStatus.setText("Impossible d'ouvrir le contenu : " + e.getMessage());
        }
    }
}
