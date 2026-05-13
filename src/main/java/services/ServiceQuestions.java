package services;

import entities.Question;
import tools.Mydb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuestions {

    private Connection getConnection() {
        return Mydb.getInstance().getConnection();
    }

    public void ajouter(Question q) {
        Connection connection = getConnection();
        if (connection == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO questions(game_id, question_text, opt1, opt2, opt3, correct_answer) VALUES (?,?,?,?,?,?)")) {
            ps.setInt(1, q.getGameId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOpt1());
            ps.setString(4, q.getOpt2());
            ps.setString(5, q.getOpt3());
            ps.setString(6, q.getCorrectAnswer());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Question q) {
        Connection connection = getConnection();
        if (connection == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE questions SET question_text=?, opt1=?, opt2=?, opt3=?, correct_answer=? WHERE id=?")) {
            ps.setString(1, q.getQuestionText());
            ps.setString(2, q.getOpt1());
            ps.setString(3, q.getOpt2());
            ps.setString(4, q.getOpt3());
            ps.setString(5, q.getCorrectAnswer());
            ps.setInt(6, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Question> getByGameId(int gameId) {
        List<Question> questions = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) return questions;
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM questions WHERE game_id = ?")) {
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    questions.add(new Question(
                            rs.getInt("id"),
                            rs.getInt("game_id"),
                            rs.getString("question_text"),
                            rs.getString("opt1"),
                            rs.getString("opt2"),
                            rs.getString("opt3"),
                            rs.getString("correct_answer")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public void supprimer(int id) {
        Connection connection = getConnection();
        if (connection == null) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
