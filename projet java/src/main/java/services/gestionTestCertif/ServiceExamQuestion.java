package services.gestionTestCertif;

import interfaces.IService;
import models.gestionTestCertif.ExamQuestion;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceExamQuestion implements IService<ExamQuestion> {

    @Override
    public void add(ExamQuestion q) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(
                "INSERT INTO `exam_question`(`exam_id`, `text`, `options`, `correct_option_index`, `points`) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, q.getExamId()); ps.setString(2, q.getText());
            ps.setString(3, String.join(";", q.getOptions()));
            ps.setInt(4, q.getCorrectOptionIndex()); ps.setInt(5, q.getPoints());
            ps.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    @Override
    public List<ExamQuestion> getAll() {
        List<ExamQuestion> list = new ArrayList<>();
        try {
            ResultSet rs = MyDataBase.getInstance().getCnx().createStatement().executeQuery("SELECT * FROM `exam_question`");
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return list;
    }

    @Override
    public ExamQuestion getById(int id) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement("SELECT * FROM `exam_question` WHERE `id`=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return null;
    }

    public List<ExamQuestion> getByExam(int examId) {
        List<ExamQuestion> list = new ArrayList<>();
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement("SELECT * FROM `exam_question` WHERE `exam_id` = ?");
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return list;
    }

    @Override
    public void update(ExamQuestion q) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement(
                "UPDATE `exam_question` SET `text`=?, `options`=?, `correct_option_index`=?, `points`=? WHERE `id`=?");
            ps.setString(1, q.getText()); ps.setString(2, String.join(";", q.getOptions()));
            ps.setInt(3, q.getCorrectOptionIndex()); ps.setInt(4, q.getPoints()); ps.setInt(5, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    @Override
    public void delete(ExamQuestion q) {
        try {
            PreparedStatement ps = MyDataBase.getInstance().getCnx().prepareStatement("DELETE FROM `exam_question` WHERE `id`=?");
            ps.setInt(1, q.getId()); ps.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    private ExamQuestion mapRow(ResultSet rs) throws SQLException {
        ExamQuestion q = new ExamQuestion();
        q.setId(rs.getInt("id")); q.setExamId(rs.getInt("exam_id"));
        q.setText(rs.getString("text"));
        q.setOptions(Arrays.asList(rs.getString("options").split(";")));
        q.setCorrectOptionIndex(rs.getInt("correct_option_index"));
        q.setPoints(rs.getInt("points"));
        return q;
    }
}
