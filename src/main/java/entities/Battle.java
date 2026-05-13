package entities;

import java.time.LocalDateTime;

public class Battle {
    private int id;
    private String battleType; // duel, team, ia
    private String status;     // waiting, ongoing, finished
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String gagnant;

    // Constructeur vide
    public Battle() {}

    // Constructeur complet
    public Battle(int id, String battleType, String status, LocalDateTime startTime, LocalDateTime endTime, String gagnant) {
        this.id = id;
        this.battleType = battleType;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gagnant = gagnant;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBattleType() {
        return battleType;
    }

    public void setBattleType(String battleType) {
        this.battleType = battleType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getGagnant() {
        return gagnant;
    }

    public void setGagnant(String gagnant) {
        this.gagnant = gagnant;
    }

    @Override
    public String toString() {
        return "Battle{" +
                "id=" + id +
                ", battleType='" + battleType + '\'' +
                ", status='" + status + '\'' +
                ", gagnant='" + gagnant + '\'' +
                '}';
    }
}
