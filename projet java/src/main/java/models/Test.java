package models;

public class Test {
    private int id;
    private String titre;
    private int scoreMin;
    private int coursId;

    public Test() {}

    public Test(int id, String titre, int scoreMin, int coursId) {
        this.id = id;
        this.titre = titre;
        this.scoreMin = scoreMin;
        this.coursId = coursId;
    }

    public Test(String titre, int scoreMin, int coursId) {
        this.titre = titre;
        this.scoreMin = scoreMin;
        this.coursId = coursId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public int getScoreMin() { return scoreMin; }
    public void setScoreMin(int scoreMin) { this.scoreMin = scoreMin; }

    public int getCoursId() { return coursId; }
    public void setCoursId(int coursId) { this.coursId = coursId; }

    @Override
    public String toString() {
        return "Test{" + "id=" + id + ", titre='" + titre + '\'' + '}';
    }
}
