package services.gestionJeux;

import models.gestionJeux.QuizQuestion;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuestions {

    private Connection cnx() { return MyDataBase.getInstance().getCnx(); }

    public void ajouter(QuizQuestion q) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "INSERT INTO quiz_questions(game_id, question_text, opt1, opt2, opt3, correct_answer) VALUES (?,?,?,?,?,?)")) {
            ps.setInt(1, q.getGameId()); ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOpt1()); ps.setString(4, q.getOpt2());
            ps.setString(5, q.getOpt3()); ps.setString(6, q.getCorrectAnswer());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceQuestions.ajouter: " + e.getMessage()); }
    }

    public void modifier(QuizQuestion q) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "UPDATE quiz_questions SET question_text=?, opt1=?, opt2=?, opt3=?, correct_answer=? WHERE id=?")) {
            ps.setString(1, q.getQuestionText()); ps.setString(2, q.getOpt1());
            ps.setString(3, q.getOpt2()); ps.setString(4, q.getOpt3());
            ps.setString(5, q.getCorrectAnswer()); ps.setInt(6, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceQuestions.modifier: " + e.getMessage()); }
    }

    public List<QuizQuestion> getByGameId(int gameId) {
        List<QuizQuestion> list = new ArrayList<>();
        try (PreparedStatement ps = cnx().prepareStatement("SELECT * FROM quiz_questions WHERE game_id=?")) {
            ps.setInt(1, gameId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new QuizQuestion(rs.getInt("id"), rs.getInt("game_id"),
                    rs.getString("question_text"), rs.getString("opt1"),
                    rs.getString("opt2"), rs.getString("opt3"), rs.getString("correct_answer")));
        } catch (SQLException e) { System.err.println("ServiceQuestions.getByGameId: " + e.getMessage()); }
        return list;
    }

    public void supprimer(int id) {
        try (PreparedStatement ps = cnx().prepareStatement("DELETE FROM quiz_questions WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceQuestions.supprimer: " + e.getMessage()); }
    }
}
