package models;

public class Certificat {
    private int id;
    private String nom;
    private int testId;

    public Certificat() {}

    public Certificat(int id, String nom, int testId) {
        this.id = id;
        this.nom = nom;
        this.testId = testId;
    }

    public Certificat(String nom, int testId) {
        this.nom = nom;
        this.testId = testId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getTestId() { return testId; }
    public void setTestId(int testId) { this.testId = testId; }

    @Override
    public String toString() {
        return "Certificat{" + "id=" + id + ", nom='" + nom + '\'' + '}';
    }
}
