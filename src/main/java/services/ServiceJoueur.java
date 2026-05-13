package services;

import entities.Joueur;
import tools.Mydb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceJoueur implements IService<Joueur> {

    private Connection connection = Mydb.getInstance().getConnection();

    // 1. Rejoindre une battle
    public void rejoindreBattle(int userId, int battleId) {
        try {
            String sql = "INSERT INTO joueur(user_id, battle_id, score, status) VALUES (?, ?, 0, 'active')";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, battleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur rejoindreBattle: " + e.getMessage());
        }
    }

    // 2. Mettre à jour le score en temps réel
    public void updateScore(int userId, int battleId, int newScore) {
        try {
            String sql = "UPDATE joueur SET score = ? WHERE user_id = ? AND battle_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, newScore);
            ps.setInt(2, userId);
            ps.setInt(3, battleId);
            ps.executeUpdate();
            refreshRanking(battleId);
        } catch (SQLException e) {
            System.out.println("Erreur updateScore: " + e.getMessage());
        }
    }

    // 3. Système de Ranking Live
    public void refreshRanking(int battleId) {
        try {
            String sql = "SELECT id FROM joueur WHERE battle_id = ? ORDER BY score DESC";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, battleId);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                updateRank(rs.getInt("id"), rank++);
            }
        } catch (SQLException e) {
            System.out.println("Erreur refreshRanking: " + e.getMessage());
        }
    }

    private void updateRank(int joueurId, int rank) {
        try {
            String sql = "UPDATE joueur SET rank = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, rank);
            ps.setInt(2, joueurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur updateRank: " + e.getMessage());
        }
    }

    // --- Méthodes Obligatoires d'IService ---

    @Override
    public void ajouter(Joueur j) {
        rejoindreBattle(j.getUserId(), j.getBattleId());
    }

    @Override
    public void modifier(Joueur j) {
        try {
            String sql = "UPDATE joueur SET score=?, rank=?, status=? WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, j.getScore());
            ps.setInt(2, j.getRank());
            ps.setString(3, j.getStatus());
            ps.setInt(4, j.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Joueur j) {
        try {
            String sql = "DELETE FROM joueur WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, j.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Joueur> getAll() {
        List<Joueur> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM joueur";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Joueur j = new Joueur(rs.getInt("id"), rs.getInt("battle_id"), rs.getInt("user_id"), 
                                     rs.getInt("score"), rs.getInt("rank"), rs.getString("status"));
                list.add(j);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public Joueur getById(int id) {
        return null;
    }
}