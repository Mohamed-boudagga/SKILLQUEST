package entities;

public class Games {
    private int id;
    private String typeJeux;
    private String difficulte;
    private int timeLimit;
    private int scoreMax;
    private String description;

    public Games() {
    }

    public Games(int id, String typeJeux, String difficulte, int timeLimit, int scoreMax) {
        this.id = id;
        this.typeJeux = typeJeux;
        this.difficulte = difficulte;
        this.timeLimit = timeLimit;
        this.scoreMax = scoreMax;
    }

    public Games(int id, String typeJeux, String difficulte, int timeLimit, int scoreMax, String description) {
        this.id = id;
        this.typeJeux = typeJeux;
        this.difficulte = difficulte;
        this.timeLimit = timeLimit;
        this.scoreMax = scoreMax;
        this.description = description;
    }

    public Games(String typeJeux, String difficulte, int timeLimit, int scoreMax, String description) {
        this.typeJeux = typeJeux;
        this.difficulte = difficulte;
        this.timeLimit = timeLimit;
        this.scoreMax = scoreMax;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeJeux() {
        return typeJeux;
    }

    public void setTypeJeux(String typeJeux) {
        this.typeJeux = typeJeux;
    }

    public String getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(String difficulte) {
        this.difficulte = difficulte;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getScoreMax() {
        return scoreMax;
    }

    public void setScoreMax(int scoreMax) {
        this.scoreMax = scoreMax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Games{" +
                "id=" + id +
                ", typeJeux='" + typeJeux + '\'' +
                ", difficulte='" + difficulte + '\'' +
                ", timeLimit=" + timeLimit +
                ", scoreMax=" + scoreMax +
                ", description='" + description + '\'' +
                '}';
    }
}
