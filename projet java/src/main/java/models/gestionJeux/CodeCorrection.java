package models.gestionJeux;

public class CodeCorrection {
    private int id;
    private int gameId;
    private String instructions;
    private String buggyCode;
    private String correctCode;

    public CodeCorrection() {}

    public CodeCorrection(int id, int gameId, String instructions, String buggyCode, String correctCode) {
        this.id = id; this.gameId = gameId; this.instructions = instructions;
        this.buggyCode = buggyCode; this.correctCode = correctCode;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getBuggyCode() { return buggyCode; }
    public void setBuggyCode(String buggyCode) { this.buggyCode = buggyCode; }
    public String getCorrectCode() { return correctCode; }
    public void setCorrectCode(String correctCode) { this.correctCode = correctCode; }
}
