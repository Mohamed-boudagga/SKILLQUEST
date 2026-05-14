package services.gestionJeux;

import models.gestionJeux.CodeCorrection;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCodeCorrection {

    private Connection cnx() { return MyDataBase.getInstance().getCnx(); }

    public void ajouter(CodeCorrection c) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "INSERT INTO code_corrections(game_id, instructions, buggy_code, correct_code) VALUES (?,?,?,?)")) {
            ps.setInt(1, c.getGameId()); ps.setString(2, c.getInstructions());
            ps.setString(3, c.getBuggyCode()); ps.setString(4, c.getCorrectCode());
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceCodeCorrection.ajouter: " + e.getMessage()); }
    }

    public List<CodeCorrection> getByGameId(int gameId) {
        List<CodeCorrection> list = new ArrayList<>();
        try (PreparedStatement ps = cnx().prepareStatement("SELECT * FROM code_corrections WHERE game_id=?")) {
            ps.setInt(1, gameId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new CodeCorrection(rs.getInt("id"), rs.getInt("game_id"),
                    rs.getString("instructions"), rs.getString("buggy_code"), rs.getString("correct_code")));
        } catch (SQLException e) { System.err.println("ServiceCodeCorrection.getByGameId: " + e.getMessage()); }
        return list;
    }

    public void supprimer(int id) {
        try (PreparedStatement ps = cnx().prepareStatement("DELETE FROM code_corrections WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceCodeCorrection.supprimer: " + e.getMessage()); }
    }
}
