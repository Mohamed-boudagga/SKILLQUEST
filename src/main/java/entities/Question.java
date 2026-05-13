package entities;

public class Question {
    private int id;
    private int gameId;
    private String questionText;
    private String opt1;
    private String opt2;
    private String opt3;
    private String correctAnswer;

    public Question() {}

    public Question(int id, int gameId, String questionText, String opt1, String opt2, String opt3, String correctAnswer) {
        this.id = id;
        this.gameId = gameId;
        this.questionText = questionText;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = opt3;
        this.correctAnswer = correctAnswer;
    }

    public Question(int gameId, String questionText, String opt1, String opt2, String opt3, String correctAnswer) {
        this.gameId = gameId;
        this.questionText = questionText;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = opt3;
        this.correctAnswer = correctAnswer;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOpt1() { return opt1; }
    public void setOpt1(String opt1) { this.opt1 = opt1; }
    public String getOpt2() { return opt2; }
    public void setOpt2(String opt2) { this.opt2 = opt2; }
    public String getOpt3() { return opt3; }
    public void setOpt3(String opt3) { this.opt3 = opt3; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
