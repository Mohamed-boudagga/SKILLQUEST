package models;

public class Jeu {
    private int id;
    private String nom;
    private String type; // 'BATTLE' ou 'QUIZ'
    private String description;

    public Jeu() {}

    public Jeu(int id, String nom, String type, String description) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.description = description;
    }

    public Jeu(String nom, String type, String description) {
        this.nom = nom;
        this.type = type;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Jeu{" + "id=" + id + ", nom='" + nom + '\'' + ", type='" + type + '\'' + '}';
    }
}
