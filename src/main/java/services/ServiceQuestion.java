package services;

import interfaces.IService;
import models.Question;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceQuestion implements IService<Question> {

    @Override
    public void add(Question q) {
        String req = "INSERT INTO `question`(`exam_id`, `text`, `options`, `correct_option_index`, `points`) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, q.getExamId());
            ps.setString(2, q.getText());
            ps.setString(3, String.join(";", q.getOptions()));
            ps.setInt(4, q.getCorrectOptionIndex());
            ps.setInt(5, q.getPoints());
            ps.executeUpdate();
            System.out.println("Question ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Question> getAll() {
        List<Question> questions = new ArrayList<>();
        String req = "SELECT * FROM `question`";
        try {
            Statement stm = MyDataBase.getInstance().getCnx().createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setExamId(rs.getInt("exam_id"));
                q.setText(rs.getString("text"));
                q.setOptions(Arrays.asList(rs.getString("options").split(";")));
                q.setCorrectOptionIndex(rs.getInt("correct_option_index"));
                q.setPoints(rs.getInt("points"));
                questions.add(q);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return questions;
    }

    public List<Question> getByExam(int examId) {
        List<Question> questions = new ArrayList<>();
        String req = "SELECT * FROM `question` WHERE `exam_id` = ?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setExamId(rs.getInt("exam_id"));
                q.setText(rs.getString("text"));
                q.setOptions(Arrays.asList(rs.getString("options").split(";")));
                q.setCorrectOptionIndex(rs.getInt("correct_option_index"));
                q.setPoints(rs.getInt("points"));
                questions.add(q);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return questions;
    }

    @Override
    public void update(Question q) {
        String req = "UPDATE `question` SET `text`=?, `options`=?, `correct_option_index`=?, `points`=? WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setString(1, q.getText());
            ps.setString(2, String.join(";", q.getOptions()));
            ps.setInt(3, q.getCorrectOptionIndex());
            ps.setInt(4, q.getPoints());
            ps.setInt(5, q.getId());
            ps.executeUpdate();
            System.out.println("Question mise à jour avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Question q) {
        String req = "DELETE FROM `question` WHERE `id`=?";
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(req);
            ps.setInt(1, q.getId());
            ps.executeUpdate();
            System.out.println("Question supprimée avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
