package services.gestionCours;

import models.gestionCours.Cours;
import models.gestionCours.Lecon;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeconService {

    private Connection getCnx() {
        return MyDataBase.getInstance().getCnx();
    }

    public void add(Lecon lecon) throws SQLException {
        String sql = "INSERT INTO `lecon`(`titre`, `description`, `idcours`) VALUES (?, ?, ?)";
        PreparedStatement ps = getCnx().prepareStatement(sql);
        ps.setString(1, lecon.getTitre());
        ps.setString(2, lecon.getDescription());
        ps.setInt(3, lecon.getCours().getId());
        ps.executeUpdate();
    }

    public void update(Lecon lecon) throws SQLException {
        String sql = "UPDATE `lecon` SET `titre`=?, `description`=?, `idcours`=? WHERE `id`=?";
        PreparedStatement ps = getCnx().prepareStatement(sql);
        ps.setString(1, lecon.getTitre()); ps.setString(2, lecon.getDescription());
        ps.setInt(3, lecon.getCours().getId()); ps.setInt(4, lecon.getId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = getCnx().prepareStatement("DELETE FROM `lecon` WHERE `id`=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Lecon> getAll() throws SQLException {
        List<Lecon> lecons = new ArrayList<>();
        ResultSet rs = getCnx().createStatement().executeQuery("SELECT * FROM `lecon`");
        while (rs.next()) lecons.add(mapLecon(rs));
        return lecons;
    }

    public Lecon getById(int id) throws SQLException {
        PreparedStatement ps = getCnx().prepareStatement("SELECT * FROM `lecon` WHERE `id`=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapLecon(rs);
        return null;
    }

    public List<Lecon> getByCours(int coursId) throws SQLException {
        List<Lecon> lecons = new ArrayList<>();
        PreparedStatement ps = getCnx().prepareStatement("SELECT * FROM `lecon` WHERE `idcours`=?");
        ps.setInt(1, coursId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) lecons.add(mapLecon(rs));
        return lecons;
    }

    private Lecon mapLecon(ResultSet rs) throws SQLException {
        Lecon lecon = new Lecon();
        lecon.setId(rs.getInt("id"));
        lecon.setTitre(rs.getString("titre"));
        lecon.setDescription(rs.getString("description"));
        Cours cours = new Cours();
        cours.setId(rs.getInt("idcours"));
        lecon.setCours(cours);
        return lecon;
    }
}
