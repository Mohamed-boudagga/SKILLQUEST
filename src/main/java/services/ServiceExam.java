package services;

import interfaces.IService;
import models.Exam;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExam implements IService<Exam> {

    @Override
    public void add(Exam e) {
        String req = "INSERT INTO `exam`(`nom`, `level`, `dureeMinutes`) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, e.getNom());
            ps.setInt(2, e.getLevel());
            ps.setInt(3, e.getDureeMinutes());
            ps.executeUpdate();
            System.out.println("Exam ajouté avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public List<Exam> getAll() {
        List<Exam> exams = new ArrayList<>();
        String req = "SELECT * FROM `exam`";
        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Exam ex = new Exam();
                ex.setId(rs.getInt("id"));
                ex.setNom(rs.getString("nom"));
                ex.setLevel(rs.getInt("level"));
                ex.setDureeMinutes(rs.getInt("dureeMinutes"));
                exams.add(ex);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return exams;
    }

    @Override
    public void update(Exam e) {
        String req = "UPDATE `exam` SET `nom`=?, `level`=?, `dureeMinutes`=? WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, e.getNom());
            ps.setInt(2, e.getLevel());
            ps.setInt(3, e.getDureeMinutes());
            ps.setInt(4, e.getId());
            ps.executeUpdate();
            System.out.println("Exam mis à jour avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void delete(Exam e) {
        String req = "DELETE FROM `exam` WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, e.getId());
            ps.executeUpdate();
            System.out.println("Exam supprimé avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Méthode bonus : chercher par niveau
    public List<Exam> getByLevel(int level) {
        List<Exam> exams = new ArrayList<>();
        String req = "SELECT * FROM `exam` WHERE `level`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, level);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Exam ex = new Exam();
                ex.setId(rs.getInt("id"));
                ex.setNom(rs.getString("nom"));
                ex.setLevel(rs.getInt("level"));
                ex.setDureeMinutes(rs.getInt("dureeMinutes"));
                exams.add(ex);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return exams;
    }
}
