package services;

import interfaces.IService;
import models.Jeu;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceJeu implements IService<Jeu> {
    private Connection cnx;

    public ServiceJeu() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Jeu j) {
        String req = "INSERT INTO `jeu` (`nom`, `type`, `description`) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, j.getNom());
            ps.setString(2, j.getType());
            ps.setString(3, j.getDescription());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur add Jeu : " + ex.getMessage());
        }
    }

    @Override
    public List<Jeu> getAll() {
        List<Jeu> liste = new ArrayList<>();
        String req = "SELECT * FROM `jeu`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                liste.add(new Jeu(rs.getInt("id"), rs.getString("nom"), rs.getString("type"), rs.getString("description")));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getAll Jeu : " + ex.getMessage());
        }
        return liste;
    }

    @Override
    public Jeu getById(int id) {
        String req = "SELECT * FROM `jeu` WHERE `id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Jeu(rs.getInt("id"), rs.getString("nom"), rs.getString("type"), rs.getString("description"));
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getById Jeu : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void update(Jeu j) {
        String req = "UPDATE `jeu` SET `nom`=?, `type`=?, `description`=? WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, j.getNom());
            ps.setString(2, j.getType());
            ps.setString(3, j.getDescription());
            ps.setInt(4, j.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur update Jeu : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Jeu j) {
        String req = "DELETE FROM `jeu` WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, j.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur delete Jeu : " + ex.getMessage());
        }
    }
}
