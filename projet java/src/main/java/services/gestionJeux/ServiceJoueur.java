package services.gestionJeux;

import models.gestionJeux.Joueur;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceJoueur {

    private Connection cnx() { return MyDataBase.getInstance().getCnx(); }

    public void rejoindreBattle(int userId, int battleId) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "INSERT INTO joueur(user_id, battle_id, score, status) VALUES (?,?,0,'active')")) {
            ps.setInt(1, userId); ps.setInt(2, battleId); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceJoueur.rejoindre: " + e.getMessage()); }
    }

    public void updateScore(int userId, int battleId, int newScore) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "UPDATE joueur SET score=? WHERE user_id=? AND battle_id=?")) {
            ps.setInt(1, newScore); ps.setInt(2, userId); ps.setInt(3, battleId); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceJoueur.updateScore: " + e.getMessage()); }
    }

    public List<Joueur> getByBattle(int battleId) {
        List<Joueur> list = new ArrayList<>();
        try (PreparedStatement ps = cnx().prepareStatement("SELECT * FROM joueur WHERE battle_id=? ORDER BY score DESC")) {
            ps.setInt(1, battleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Joueur(rs.getInt("id"), rs.getInt("battle_id"),
                    rs.getInt("user_id"), rs.getInt("score"), rs.getInt("rank"), rs.getString("status")));
        } catch (SQLException e) { System.err.println("ServiceJoueur.getByBattle: " + e.getMessage()); }
        return list;
    }
}
