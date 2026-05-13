package services;

import interfaces.IService;
import models.Certification;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCertification implements IService<Certification> {

    @Override
    public void add(Certification c) {
        String req = "INSERT INTO `certification`(`title`, `level`, `dateObtention`, `description`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, c.getTitle());
            ps.setInt(2, c.getLevel());
            ps.setDate(3, new java.sql.Date(c.getDateObtention().getTime()));
            ps.setString(4, c.getDescription());
            ps.executeUpdate();
            System.out.println("Certification ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Certification> getAll() {
        List<Certification> certifications = new ArrayList<>();
        String req = "SELECT * FROM `certification`";
        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Certification c = new Certification();
                c.setId(rs.getInt("id"));
                c.setTitle(rs.getString("title"));
                c.setLevel(rs.getInt("level"));
                c.setDateObtention(rs.getDate("dateObtention"));
                c.setDescription(rs.getString("description"));
                certifications.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return certifications;
    }

    @Override
    public void update(Certification c) {
        String req = "UPDATE `certification` SET `title`=?, `level`=?, `dateObtention`=?, `description`=? WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, c.getTitle());
            ps.setInt(2, c.getLevel());
            ps.setDate(3, new java.sql.Date(c.getDateObtention().getTime()));
            ps.setString(4, c.getDescription());
            ps.setInt(5, c.getId());
            ps.executeUpdate();
            System.out.println("Certification mise à jour avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Certification c) {
        String req = "DELETE FROM `certification` WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, c.getId());
            ps.executeUpdate();
            System.out.println("Certification supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode bonus : chercher par ID
    public Certification getById(int id) {
        String req = "SELECT * FROM `certification` WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Certification c = new Certification();
                c.setId(rs.getInt("id"));
                c.setTitle(rs.getString("title"));
                c.setLevel(rs.getInt("level"));
                c.setDateObtention(rs.getDate("dateObtention"));
                c.setDescription(rs.getString("description"));
                return c;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}