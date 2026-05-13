package models;

public class Exam {

    private int id;
    private String nom;
    private int level;
    private int dureeMinutes;

    // ===== CONSTRUCTEURS =====
    public Exam() {
    }

    public Exam(int id, String nom, int level, int dureeMinutes) {
        this.id = id;
        this.nom = nom;
        this.level = level;
        this.dureeMinutes = dureeMinutes;
    }

    public Exam(String nom, int level, int dureeMinutes) {
        this.nom = nom;
        this.level = level;
        this.dureeMinutes = dureeMinutes;
    }

    // ===== GETTERS & SETTERS =====
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    // ===== TO STRING =====
    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", level=" + level +
                '}';
    }
}
