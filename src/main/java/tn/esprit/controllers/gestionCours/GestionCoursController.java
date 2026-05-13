package tn.esprit.controllers.gestionCours;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import tn.esprit.enties.Cours;
import tn.esprit.services.CoursService;
import tn.esprit.utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GestionCoursController {

    @FXML
    private TableView<Cours> tableCours;

    @FXML
    private TableColumn<Cours, String> colTitre;

    @FXML
    private TableColumn<Cours, String> colDescription;

    @FXML
    private TableColumn<Cours, String> colNiveau;

    @FXML
    private TableColumn<Cours, String> colContenue;

    @FXML
    private TableColumn<Cours, String> colIdAjouteur;

    @FXML
    private TableColumn<Cours, String> colDateDeCreation;

    @FXML
    private TableColumn<Cours, Void> colAction;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> comboFilter;

    @FXML
    private ComboBox<String> comboSort;

    private List<Cours> allCours;
    private ObservableList<Cours> coursObservableList;
    private FilteredList<Cours> filteredCours;
    private SortedList<Cours> sortedCours;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // ===== INIT =====
    @FXML
    public void initialize() {

        allCours = new ArrayList<>();

        // 🔥 FETCH DB
        fetchListCours();

        // 🔗 BIND COLUMNS
        colTitre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitre()));

        colDescription.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));

        colNiveau.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNiveau()));

        colContenue.setCellValueFactory(data ->
                new SimpleStringProperty(valueOrEmpty(data.getValue().getContenue())));

        colIdAjouteur.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdAjouteur())));

        colDateDeCreation.setCellValueFactory(data ->
                new SimpleStringProperty(formatDate(data.getValue().getDateDeCreation())));

        // 🔥 DISPLAY DATA
        coursObservableList = FXCollections.observableArrayList(allCours);
        filteredCours = new FilteredList<>(coursObservableList, cours -> true);
        sortedCours = new SortedList<>(filteredCours);
        tableCours.setItems(sortedCours);
        configureFilters();

        // 🔥 ACTION COLUMN
        addActionButtons();
    }

    // ===== FETCH DATA =====
    public void fetchListCours() {
        CoursService coursService = new CoursService();
        try {
            List<Cours> data = coursService.getAll();
            allCours.clear();
            allCours.addAll(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureFilters() {
        comboFilter.getItems().addAll("Tous", "Titre", "Niveau", "Contenue", "Date d'ajout", "Id Ajouteur");
        comboFilter.setValue("Tous");

        comboSort.getItems().addAll(
                "Aucun tri",
                "Niveau croissant",
                "Niveau decroissant",
                "Date recente",
                "Date ancienne",
                "Titre A-Z",
                "Titre Z-A"
        );
        comboSort.setValue("Aucun tri");

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        comboFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        comboSort.valueProperty().addListener((observable, oldValue, newValue) -> applySort());
    }

    private boolean matchesCourse(Cours cours, String keyword) {
        String filter = comboFilter.getValue();

        if ("Titre".equals(filter)) {
            return contains(cours.getTitre(), keyword);
        }

        if ("Niveau".equals(filter)) {
            return matchesNiveau(cours, keyword);
        }

        if ("Contenue".equals(filter)) {
            return contains(cours.getContenue(), keyword);
        }

        if ("Date d'ajout".equals(filter)) {
            return matchesDate(cours.getDateDeCreation(), keyword);
        }

        if ("Id Ajouteur".equals(filter)) {
            return contains(String.valueOf(cours.getIdAjouteur()), keyword);
        }

        return contains(cours.getTitre(), keyword)
                || contains(cours.getDescription(), keyword)
                || contains(cours.getContenue(), keyword)
                || matchesNiveau(cours, keyword)
                || contains(String.valueOf(cours.getIdAjouteur()), keyword)
                || matchesDate(cours.getDateDeCreation(), keyword);
    }

    private void applyFilters() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();

        filteredCours.setPredicate(cours -> {
            if (keyword.isEmpty()) {
                return true;
            }

            return matchesCourse(cours, keyword);
        });

        applySort();
    }

    private void applySort() {
        String sort = comboSort.getValue();

        if ("Niveau croissant".equals(sort)) {
            sortedCours.setComparator(Comparator.comparingInt(this::niveauAsInt));
        } else if ("Niveau decroissant".equals(sort)) {
            sortedCours.setComparator(Comparator.comparingInt(this::niveauAsInt).reversed());
        } else if ("Date recente".equals(sort)) {
            sortedCours.setComparator(Comparator.comparing(Cours::getDateDeCreation, Comparator.nullsLast(Date::compareTo)).reversed());
        } else if ("Date ancienne".equals(sort)) {
            sortedCours.setComparator(Comparator.comparing(Cours::getDateDeCreation, Comparator.nullsLast(Date::compareTo)));
        } else if ("Titre A-Z".equals(sort)) {
            sortedCours.setComparator(Comparator.comparing(cours -> valueOrEmpty(cours.getTitre()).toLowerCase()));
        } else if ("Titre Z-A".equals(sort)) {
            sortedCours.setComparator(Comparator.comparing((Cours cours) -> valueOrEmpty(cours.getTitre()).toLowerCase()).reversed());
        } else {
            sortedCours.setComparator(null);
        }
    }

    private int niveauAsInt(Cours cours) {
        try {
            return Integer.parseInt(valueOrEmpty(cours.getNiveau()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @FXML
    private void resetFilters() {
        comboFilter.setValue("Tous");
        comboSort.setValue("Aucun tri");
        txtSearch.clear();
        applyFilters();
    }

    private boolean matchesNiveau(Cours cours, String keyword) {
        String niveau = valueOrEmpty(cours.getNiveau()).toLowerCase();

        if (keyword.matches("\\d+")) {
            return niveau.equals(keyword);
        }

        return niveau.contains(keyword);
    }

    private boolean matchesDate(Date date, String keyword) {
        return contains(formatDate(date), keyword)
                || contains(formatDisplayDate(date), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String formatDate(Date date) {
        return date == null ? "" : dateFormat.format(date);
    }

    private String formatDisplayDate(Date date) {
        return date == null ? "" : displayDateFormat.format(date);
    }

    // ===== ACTION COLUMN =====
    private void addActionButtons() {

        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button btnUpdate = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final Button btnAddLecon = new Button("Ajouter un Lecon");

            {
                btnUpdate.getStyleClass().add("btn-update");
                btnDelete.getStyleClass().add("btn-delete");
                btnAddLecon.getStyleClass().add("btn-addLecon");
                btnAddLecon.setOnAction(event -> {

                Cours c = getTableView().getItems().get(getIndex());
                System.out.println("Update : " + c);

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionCours/updateCours.fxml"));
                    Parent root = loader.load();

                    // 👉 récupérer le controller update
                    UpdateCours controller = loader.getController();

                    // 👉 envoyer le cours sélectionné
                    controller.setCours(c);

                    // 👉 changer de page
                    tableCours.getScene().setRoot(root);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
                btnUpdate.setOnAction(event -> {

                    Cours c = getTableView().getItems().get(getIndex());
                    System.out.println("Update : " + c);

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionCours/updateCours.fxml"));
                        Parent root = loader.load();

                        // 👉 récupérer le controller update
                        UpdateCours controller = loader.getController();

                        // 👉 envoyer le cours sélectionné
                        controller.setCours(c);

                        // 👉 changer de page
                        tableCours.getScene().setRoot(root);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                btnDelete.setOnAction(event -> {

                    Cours c = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation suppression");
                    alert.setHeaderText(null);
                    alert.setContentText("Voulez-vous supprimer ce cours ?");

                    if (alert.showAndWait().get() == ButtonType.OK) {

                        try {
                            new CoursService().delete(c.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        allCours.remove(c);
                        coursObservableList.remove(c);
                    }
                });   }

            private final HBox pane = new HBox(10, btnUpdate, btnDelete);

            {
                pane.setStyle("-fx-alignment: center;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    // ===== NAVIGATION =====
    @FXML
    private void goToAddCours() {
        try {
            SessionManager.setAddCourseReturnPath("/gestionCours/GestionCours.fxml");
            Parent parent = FXMLLoader.load(getClass().getResource("/gestionCours/addCours.fxml"));
            tableCours.getScene().setRoot(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
