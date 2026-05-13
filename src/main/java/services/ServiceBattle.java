package services;

import entities.Battle;
import tools.Mydb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.sql.SQLException;

public class ServiceBattle implements IService<Battle> {

    private Connection connection = Mydb.getInstance().getConnection();

    // --- Méthodes Spécifiques à votre logique de jeu ---

    public int createBattle(String type) {
        try {
            String sql = "INSERT INTO battle(battle_type, status) VALUES (?, 'waiting')";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, type);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Erreur createBattle: " + e.getMessage());
        }
        return -1;
    }

    public void startBattle(int battleId) {
        try {
            String sql = "UPDATE battle SET status = 'ongoing', start_time = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, battleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur startBattle: " + e.getMessage());
        }
    }

    public void finishBattle(int battleId) {
        try {
            String sql = "UPDATE battle SET status = 'finished', end_time = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, battleId);
            ps.executeUpdate();
            determineWinner(battleId);
        } catch (SQLException e) {
            System.out.println("Erreur finishBattle: " + e.getMessage());
        }
    }

    private void determineWinner(int battleId) {
        try {
            String sql = "SELECT user_id FROM joueur WHERE battle_id = ? ORDER BY score DESC LIMIT 1";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, battleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int winnerId = rs.getInt("user_id");
                String updateSql = "UPDATE battle SET gagnant = ? WHERE id = ?";
                PreparedStatement psUpdate = connection.prepareStatement(updateSql);
                psUpdate.setString(1, "User ID: " + winnerId);
                psUpdate.setInt(2, battleId);
                psUpdate.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Erreur determineWinner: " + e.getMessage());
        }
    }

    // --- Méthodes Obligatoires d'IService (pour corriger le Build) ---

    @Override
    public void ajouter(Battle b) {
        try {
            String sql = "INSERT INTO battle(battle_type, status, start_time, end_time, gagnant) VALUES (?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, b.getBattleType());
            ps.setString(2, b.getStatus());
            ps.setTimestamp(3, b.getStartTime() != null ? Timestamp.valueOf(b.getStartTime()) : null);
            ps.setTimestamp(4, b.getEndTime() != null ? Timestamp.valueOf(b.getEndTime()) : null);
            ps.setString(5, b.getGagnant());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur ajouter Battle: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Battle b) {
        try {
            String sql = "UPDATE battle SET battle_type=?, status=?, gagnant=? WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, b.getBattleType());
            ps.setString(2, b.getStatus());
            ps.setString(3, b.getGagnant());
            ps.setInt(4, b.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Battle b) {
        try {
            String sql = "DELETE FROM battle WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, b.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Battle> getAll() {
        List<Battle> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM battle";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Battle b = new Battle(rs.getInt("id"), rs.getString("battle_type"), rs.getString("status"), null, null, rs.getString("gagnant"));
                Timestamp start = rs.getTimestamp("start_time");
                if (start != null) b.setStartTime(start.toLocalDateTime());
                Timestamp end = rs.getTimestamp("end_time");
                if (end != null) b.setEndTime(end.toLocalDateTime());
                b.setGagnant(rs.getString("gagnant"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public Battle getById(int id) {
        return null;
    }
}