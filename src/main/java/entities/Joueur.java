package entities;

public class Joueur {
    private int id;
    private int battleId;
    private int userId;
    private int score;
    private int rank;
    private String status; // active, eliminated, winner

    public Joueur() {}

    public Joueur(int id, int battleId, int userId, int score, int rank, String status) {
        this.id = id;
        this.battleId = battleId;
        this.userId = userId;
        this.score = score;
        this.rank = rank;
        this.status = status;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBattleId() { return battleId; }
    public void setBattleId(int battleId) { this.battleId = battleId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Joueur{" + "id=" + id + ", battleId=" + battleId + ", score=" + score + ", rank=" + rank + ", status='" + status + '\'' + '}';
    }
}
