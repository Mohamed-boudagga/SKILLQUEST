package models;

import java.util.Date;

public class Certification {
    private int id;
    private String title;
    private int level;
    private Date dateObtention;
    private String description;

    public Certification() {
    }

    public Certification(int id, String title, int level, Date dateObtention, String description) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.dateObtention = dateObtention;
        this.description = description;
    }

    public Certification(String title, int level, Date dateObtention, String description) {
        this.title = title;
        this.level = level;
        this.dateObtention = dateObtention;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Date getDateObtention() {
        return dateObtention;
    }

    public void setDateObtention(Date dateObtention) {
        this.dateObtention = dateObtention;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Certification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", level=" + level +
                ", dateObtention=" + dateObtention +
                ", description='" + description + '\'' +
                "}\n";
    }
}

