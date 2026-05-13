package services;

import interfaces.IService;
import models.Certificat;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCertificat implements IService<Certificat> {
    private Connection cnx;

    public ServiceCertificat() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Certificat c) {
        String req = "INSERT INTO `certificat` (`nom`, `test_id`) VALUES (?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, c.getNom());
            ps.setInt(2, c.getTestId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur add Certificat : " + ex.getMessage());
        }
    }

    @Override
    public List<Certificat> getAll() {
        List<Certificat> liste = new ArrayList<>();
        String req = "SELECT * FROM `certificat`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                liste.add(new Certificat(rs.getInt("id"), rs.getString("nom"), rs.getInt("test_id")));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getAll Certificat : " + ex.getMessage());
        }
        return liste;
    }

    @Override
    public Certificat getById(int id) {
        String req = "SELECT * FROM `certificat` WHERE `id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Certificat(rs.getInt("id"), rs.getString("nom"), rs.getInt("test_id"));
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getById Certificat : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void update(Certificat c) {
        String req = "UPDATE `certificat` SET `nom`=?, `test_id`=? WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, c.getNom());
            ps.setInt(2, c.getTestId());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur update Certificat : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Certificat c) {
        String req = "DELETE FROM `certificat` WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, c.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur delete Certificat : " + ex.getMessage());
        }
    }
}
