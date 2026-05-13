package models.gestionCours;

import java.util.Date;

public class Cours {
    private int id;
    private int idAjouteur;
    private String titre, description, niveau, contenue;
    private Date dateDeCreation;

    public Cours() {}

    public Cours(int id, String titre, String description, String niveau) {
        this(id, titre, description, niveau, null);
    }

    public Cours(int id, String titre, String description, String niveau, String contenue) {
        this.id = id; this.titre = titre; this.description = description;
        this.niveau = niveau; this.contenue = contenue;
    }

    public Cours(String titre, String description, String niveau, String contenue, int idAjouteur) {
        this.titre = titre; this.description = description; this.niveau = niveau;
        this.contenue = contenue; this.idAjouteur = idAjouteur;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdAjouteur() { return idAjouteur; }
    public void setIdAjouteur(int idAjouteur) { this.idAjouteur = idAjouteur; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public String getContenue() { return contenue; }
    public void setContenue(String contenue) { this.contenue = contenue; }
    public Date getDateDeCreation() { return dateDeCreation; }
    public void setDateDeCreation(Date dateDeCreation) { this.dateDeCreation = dateDeCreation; }

    @Override
    public String toString() {
        return "Cours{id=" + id + ", titre='" + titre + "', niveau='" + niveau + "'}";
    }
}
