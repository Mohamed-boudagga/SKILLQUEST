package models;

public class ProgressionCours {
    private int etudiantId;
    private int coursId;
    private int pourcentage;
    private String statut; // EN_COURS, TERMINE

    // Champs pour l'affichage (JOIN)
    private String titreCours;

    public ProgressionCours() {}

    public ProgressionCours(int etudiantId, int coursId, int pourcentage, String statut) {
        this.etudiantId = etudiantId;
        this.coursId = coursId;
        this.pourcentage = pourcentage;
        this.statut = statut;
    }

    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }

    public int getCoursId() { return coursId; }
    public void setCoursId(int coursId) { this.coursId = coursId; }

    public int getPourcentage() { return pourcentage; }
    public void setPourcentage(int pourcentage) { this.pourcentage = pourcentage; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getTitreCours() { return titreCours; }
    public void setTitreCours(String titreCours) { this.titreCours = titreCours; }
}
