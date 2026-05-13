package entities;

import java.sql.Date;

public class Partie {

    private int id;
    private int score;
    private Date datee;
    private Joueur joueur; // relation avec joueur

    public Partie(int id, int score, Date datee, Joueur joueur) {
        this.id = id;
        this.score = score;
        this.datee = datee;
        this.joueur = joueur;
    }
    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public Date getDatee() {
        return datee;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDatee(Date datee) {
        this.datee = datee;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }
}