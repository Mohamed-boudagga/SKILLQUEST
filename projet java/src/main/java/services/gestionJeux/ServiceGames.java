package services.gestionJeux;

import models.gestionJeux.Games;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGames implements IServiceJeux<Games> {

    private Connection cnx() { return MyDataBase.getInstance().getCnx(); }

    @Override
    public void ajouter(Games g) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "INSERT INTO games(TypeJeux, difficulte, time_limit, ScoreMax, description) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getTypeJeux());
            ps.setString(2, g.getDifficulte());
            ps.setInt(3, g.getTimeLimit());
            ps.setInt(4, g.getScoreMax());
            ps.setString(5, g.getDescription());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) g.setId(rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("ServiceGames.ajouter: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Games g) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "UPDATE games SET TypeJeux=?, difficulte=?, time_limit=?, ScoreMax=?, description=? WHERE id=?")) {
            ps.setString(1, g.getTypeJeux()); ps.setString(2, g.getDifficulte());
            ps.setInt(3, g.getTimeLimit()); ps.setInt(4, g.getScoreMax());
            ps.setString(5, g.getDescription()); ps.setInt(6, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceGames.modifier: " + e.getMessage()); }
    }

    @Override
    public void supprimer(Games g) {
        try (PreparedStatement ps = cnx().prepareStatement("DELETE FROM games WHERE id=?")) {
            ps.setInt(1, g.getId()); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceGames.supprimer: " + e.getMessage()); }
    }

    @Override
    public List<Games> getAll() {
        List<Games> list = new ArrayList<>();
        try (Statement st = cnx().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM games ORDER BY id DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("ServiceGames.getAll: " + e.getMessage()); }
        return list;
    }

    public List<Games> findByType(String type) {
        List<Games> list = new ArrayList<>();
        try (PreparedStatement ps = cnx().prepareStatement("SELECT * FROM games WHERE TypeJeux LIKE ?")) {
            ps.setString(1, "%" + type + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("ServiceGames.findByType: " + e.getMessage()); }
        return list;
    }

    @Override
    public Games getById(int id) {
        try (PreparedStatement ps = cnx().prepareStatement("SELECT * FROM games WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { System.err.println("ServiceGames.getById: " + e.getMessage()); }
        return null;
    }

    private Games mapRow(ResultSet rs) throws SQLException {
        return new Games(rs.getInt("id"), rs.getString("TypeJeux"), rs.getString("difficulte"),
                rs.getInt("time_limit"), rs.getInt("ScoreMax"), rs.getString("description"));
    }
}
