package services.gestionTestCertif;

import interfaces.IService;
import models.gestionTestCertif.Exam;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExam implements IService<Exam> {

    @Override
    public void add(Exam e) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(
                "INSERT INTO `exam`(`nom`, `level`, `dureeMinutes`) VALUES (?, ?, ?)");
            ps.setString(1, e.getNom()); ps.setInt(2, e.getLevel()); ps.setInt(3, e.getDureeMinutes());
            ps.executeUpdate();
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    @Override
    public List<Exam> getAll() {
        List<Exam> list = new ArrayList<>();
        try {
            ResultSet rs = MyDataBase.getInstance().getCnx().createStatement().executeQuery("SELECT * FROM `exam`");
            while (rs.next()) {
                Exam ex = new Exam();
                ex.setId(rs.getInt("id")); ex.setNom(rs.getString("nom"));
                ex.setLevel(rs.getInt("level")); ex.setDureeMinutes(rs.getInt("dureeMinutes"));
                list.add(ex);
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        return list;
    }

    @Override
    public void update(Exam e) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(
                "UPDATE `exam` SET `nom`=?, `level`=?, `dureeMinutes`=? WHERE `id`=?");
            ps.setString(1, e.getNom()); ps.setInt(2, e.getLevel());
            ps.setInt(3, e.getDureeMinutes()); ps.setInt(4, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    @Override
    public void delete(Exam e) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement("DELETE FROM `exam` WHERE `id`=?");
            ps.setInt(1, e.getId()); ps.executeUpdate();
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    @Override
    public Exam getById(int id) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement("SELECT * FROM `exam` WHERE `id`=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Exam ex = new Exam();
                ex.setId(rs.getInt("id")); ex.setNom(rs.getString("nom"));
                ex.setLevel(rs.getInt("level")); ex.setDureeMinutes(rs.getInt("dureeMinutes"));
                return ex;
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        return null;
    }

    public List<Exam> getByLevel(int level) {
        List<Exam> list = new ArrayList<>();
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement("SELECT * FROM `exam` WHERE `level`=?");
            ps.setInt(1, level);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Exam ex = new Exam();
                ex.setId(rs.getInt("id")); ex.setNom(rs.getString("nom"));
                ex.setLevel(rs.getInt("level")); ex.setDureeMinutes(rs.getInt("dureeMinutes"));
                list.add(ex);
            }
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        return list;
    }
}
