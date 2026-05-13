package tn.esprit.enties;

public class Lecon {
    private int id;
    private String titre,description;
    private Cours cours;

    public Lecon() {
    }

    public Lecon(int id, String titre, String description, Cours cours) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.cours = cours;
    }

    public Lecon(String titre, String description, Cours cours) {
        this.titre = titre;
        this.description = description;
        this.cours = cours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }
;
    @Override
    public String toString() {
        return "Lecon{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", cours=" + cours +
                '}';
    }
}
