package services;

import entities.Partie;
import tools.Mydb;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ServicePartie implements IService<Partie> {

    private Connection connection = Mydb.getInstance().getConnection();

    public void ajouter(Partie p) {
        try {
            String sql = "INSERT INTO partie(score, datee, joueur_id) VALUES (?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, p.getScore());
            ps.setDate(2, p.getDatee());
            ps.setInt(3, p.getJoueur().getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Partie p) {}
    @Override
    public void supprimer(Partie p) {}
    @Override
    public java.util.List<Partie> getAll() { return new java.util.ArrayList<>(); }
    @Override
    public Partie getById(int id) { return null; }
}