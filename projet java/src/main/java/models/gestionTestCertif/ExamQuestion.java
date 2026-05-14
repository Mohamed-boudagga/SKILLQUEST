package models.gestionTestCertif;

import java.util.List;

public class ExamQuestion {
    private int id;
    private int examId;
    private String text;
    private List<String> options;
    private int correctOptionIndex;
    private int points;

    public ExamQuestion() {}

    public ExamQuestion(String text, List<String> options, int correctOptionIndex, int points) {
        this.text = text; this.options = options;
        this.correctOptionIndex = correctOptionIndex; this.points = points;
    }

    public ExamQuestion(int id, int examId, String text, List<String> options, int correctOptionIndex, int points) {
        this.id = id; this.examId = examId; this.text = text; this.options = options;
        this.correctOptionIndex = correctOptionIndex; this.points = points;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
