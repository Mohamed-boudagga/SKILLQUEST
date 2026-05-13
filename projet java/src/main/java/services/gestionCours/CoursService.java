package services.gestionCours;

import models.gestionCours.Cours;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService {

    private Connection getCnx() {
        return MyDataBase.getInstance().getCnx();
    }

    public void add(Cours cours) throws SQLException {
        validateNiveau(cours.getNiveau());
        String sql = "INSERT INTO `cours` (`titre`, `description`, `niveau`, `contenue`, `idAjouteur`, `dateDeCreation`) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = getCnx().prepareStatement(sql);
        ps.setString(1, cours.getTitre());
        ps.setString(2, cours.getDescription());
        ps.setString(3, cours.getNiveau());
        ps.setString(4, cours.getContenue());
        ps.setInt(5, cours.getIdAjouteur());
        ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        ps.executeUpdate();
    }

    public void update(Cours cours) throws SQLException {
        validateNiveau(cours.getNiveau());
        String sql = "UPDATE `cours` SET `titre`=?, `description`=?, `niveau`=?, `contenue`=?, `idAjouteur`=? WHERE `id`=?";
        PreparedStatement ps = getCnx().prepareStatement(sql);
        ps.setString(1, cours.getTitre()); ps.setString(2, cours.getDescription());
        ps.setString(3, cours.getNiveau()); ps.setString(4, cours.getContenue());
        ps.setInt(5, cours.getIdAjouteur()); ps.setInt(6, cours.getId());
        ps.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = getCnx().prepareStatement("DELETE FROM `cours` WHERE `id`=?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public List<Cours> getAll() throws SQLException {
        List<Cours> list = new ArrayList<>();
        ResultSet rs = getCnx().createStatement().executeQuery("SELECT * FROM `cours`");
        while (rs.next()) list.add(mapCours(rs));
        return list;
    }

    public Cours getById(int id) throws SQLException {
        PreparedStatement ps = getCnx().prepareStatement("SELECT * FROM `cours` WHERE `id`=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return mapCours(rs);
        return null;
    }

    private Cours mapCours(ResultSet rs) throws SQLException {
        Cours c = new Cours();
        c.setId(rs.getInt("id"));
        c.setTitre(rs.getString("titre"));
        c.setDescription(rs.getString("description"));
        try { c.setNiveau(rs.getString("niveau")); } catch (SQLException ignored) {}
        try { c.setContenue(rs.getString("contenue")); } catch (SQLException ignored) {}
        try { c.setIdAjouteur(rs.getInt("idAjouteur")); } catch (SQLException ignored) {}
        try { c.setDateDeCreation(rs.getTimestamp("dateDeCreation")); } catch (SQLException ignored) {}
        return c;
    }

    private void validateNiveau(String value) throws SQLException {
        try {
            int n = Integer.parseInt(value);
            if (n < 1 || n > 6) throw new SQLException("Le niveau doit etre entre 1 et 6.");
        } catch (NumberFormatException e) {
            throw new SQLException("Le niveau doit etre un entier entre 1 et 6.");
        }
    }
}
