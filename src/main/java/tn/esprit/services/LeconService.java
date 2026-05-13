package tn.esprit.services;

import tn.esprit.enties.Cours;
import tn.esprit.enties.Lecon;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeconService implements IService<Lecon> {
    private Connection connection = MyDataBase.getInstance().getConnection();

    @Override
    public void add(Lecon lecon) throws SQLException {
        String sql = "INSERT INTO `lecon`(`titre`, `description`, `idcours`) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, lecon.getTitre());
        ps.setString(2, lecon.getDescription());
        ps.setInt(3, lecon.getCours().getId());
        ps.executeUpdate();
        System.out.println("Lecon ajoutée !");
    }

    @Override
    public void update(Lecon lecon) throws SQLException {
        String sql = "UPDATE `lecon` SET `titre`=?, `description`=?, `idcours`=? WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, lecon.getTitre());
        ps.setString(2, lecon.getDescription());
        ps.setInt(3, lecon.getCours().getId());
        ps.setInt(4, lecon.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `lecon` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Lecon> getAll() throws SQLException {
        List<Lecon> lecons = new ArrayList<>();
        String sql = "SELECT * FROM `lecon`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Lecon lecon = new Lecon();
            lecon.setId(rs.getInt(1));
            lecon.setTitre(rs.getString(2));
            lecon.setDescription(rs.getString(3));
            // fetch the related Cours by id stored in column 4
            Cours cours = new Cours();
            cours.setId(rs.getInt(4));
            lecon.setCours(cours);
            lecons.add(lecon);
        }
        return lecons;
    }
    public Lecon getById(int id) throws SQLException {
        String sql = "SELECT * FROM `lecon` WHERE `id`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Lecon lecon = new Lecon();
            lecon.setId(rs.getInt(1));
            lecon.setTitre(rs.getString(2));
            lecon.setDescription(rs.getString(3));
            Cours cours = new Cours();
            cours.setId(rs.getInt(4));
            lecon.setCours(cours);
            return lecon;
        }
        return null;
    }

    public Lecon getByTitre(String titre) throws SQLException {
        String sql = "SELECT * FROM `lecon` WHERE `titre`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, titre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Lecon lecon = new Lecon();
            lecon.setId(rs.getInt(1));
            lecon.setTitre(rs.getString(2));
            lecon.setDescription(rs.getString(3));
            Cours cours = new Cours();
            cours.setId(rs.getInt(4));
            lecon.setCours(cours);
            return lecon;
        }
        return null;
    }

    public Lecon getByDescription(String description) throws SQLException {
        String sql = "SELECT * FROM `lecon` WHERE `description`=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, description);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Lecon lecon = new Lecon();
            lecon.setId(rs.getInt(1));
            lecon.setTitre(rs.getString(2));
            lecon.setDescription(rs.getString(3));
            Cours cours = new Cours();
            cours.setId(rs.getInt(4));
            lecon.setCours(cours);
            return lecon;
        }
        return null;
    }
}