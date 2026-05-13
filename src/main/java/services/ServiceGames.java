package services;

import entities.Games;
import tools.Mydb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceGames implements IService<Games> {

    private Connection getConnection() {
        return Mydb.getInstance().getConnection();
    }

    @Override
    public void ajouter(Games g) {
        Connection connection = getConnection();
        if (connection == null) {
            throw new RuntimeException("Connexion à la base de données impossible.");
        }
        try {
            String sql = "INSERT INTO games(TypeJeux, difficulte, time_limit, ScoreMax, description) VALUES (?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, g.getTypeJeux());
            ps.setString(2, g.getDifficulte());
            ps.setInt(3, g.getTimeLimit());
            ps.setInt(4, g.getScoreMax());
            ps.setString(5, g.getDescription());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                g.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Games g) {
        Connection connection = getConnection();
        if (connection == null) {
            throw new RuntimeException("Connexion à la base de données impossible.");
        }
        try {
            String sql = "UPDATE games SET TypeJeux=?, difficulte=?, time_limit=?, ScoreMax=?, description=? WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, g.getTypeJeux());
            ps.setString(2, g.getDifficulte());
            ps.setInt(3, g.getTimeLimit());
            ps.setInt(4, g.getScoreMax());
            ps.setString(5, g.getDescription());
            ps.setInt(6, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Games g) {
        Connection connection = getConnection();
        if (connection == null) {
            throw new RuntimeException("Connexion à la base de données impossible.");
        }
        try {
            String sql = "DELETE FROM games WHERE id= ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Games> getAll() {
        List<Games> games = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) return games;
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM games ORDER BY id DESC")) {
            while (rs.next()) {
                games.add(new Games(
                        rs.getInt("id"),
                        rs.getString("TypeJeux"),
                        rs.getString("difficulte"),
                        rs.getInt("time_limit"),
                        rs.getInt("ScoreMax"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des jeux : " + e.getMessage());
        }
        return games;
    }

    public List<Games> findByType(String type) {
        List<Games> games = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) return games;
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM games WHERE TypeJeux LIKE ? ORDER BY id DESC")) {
            ps.setString(1, "%" + type + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    games.add(new Games(
                            rs.getInt("id"),
                            rs.getString("TypeJeux"),
                            rs.getString("difficulte"),
                            rs.getInt("time_limit"),
                            rs.getInt("ScoreMax"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return games;
    }


    @Override
    public Games getById(int id) {
        Connection connection = getConnection();
        if (connection == null) return null;
        try {
            String sql = "SELECT * FROM games WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Games(
                        rs.getInt("id"),
                        rs.getString("TypeJeux"),
                        rs.getString("difficulte"),
                        rs.getInt("time_limit"),
                        rs.getInt("ScoreMax")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération par ID : " + e.getMessage());
        }
        return null;
    }
}