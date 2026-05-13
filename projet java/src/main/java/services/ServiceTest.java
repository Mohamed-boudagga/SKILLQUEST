package services;

import interfaces.IService;
import models.Test;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceTest implements IService<Test> {
    private Connection cnx;

    public ServiceTest() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Test t) {
        String req = "INSERT INTO `test` (`titre`, `score_min`, `cours_id`) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, t.getTitre());
            ps.setInt(2, t.getScoreMin());
            ps.setInt(3, t.getCoursId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur add Test : " + ex.getMessage());
        }
    }

    @Override
    public List<Test> getAll() {
        List<Test> liste = new ArrayList<>();
        String req = "SELECT * FROM `test`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                liste.add(new Test(rs.getInt("id"), rs.getString("titre"), rs.getInt("score_min"), rs.getInt("cours_id")));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getAll Test : " + ex.getMessage());
        }
        return liste;
    }

    @Override
    public Test getById(int id) {
        String req = "SELECT * FROM `test` WHERE `id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new Test(rs.getInt("id"), rs.getString("titre"), rs.getInt("score_min"), rs.getInt("cours_id"));
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getById Test : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void update(Test t) {
        String req = "UPDATE `test` SET `titre`=?, `score_min`=?, `cours_id`=? WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, t.getTitre());
            ps.setInt(2, t.getScoreMin());
            ps.setInt(3, t.getCoursId());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur update Test : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Test t) {
        String req = "DELETE FROM `test` WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, t.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur delete Test : " + ex.getMessage());
        }
    }
}
