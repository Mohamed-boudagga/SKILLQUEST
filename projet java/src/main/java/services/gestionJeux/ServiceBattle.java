package services.gestionJeux;

import models.gestionJeux.Battle;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceBattle {

    private Connection cnx() { return MyDataBase.getInstance().getCnx(); }

    public int createBattle(String type) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "INSERT INTO battle(battle_type, status) VALUES (?, 'waiting')", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type); ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("ServiceBattle.createBattle: " + e.getMessage()); }
        return -1;
    }

    public void startBattle(int battleId) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "UPDATE battle SET status='ongoing', start_time=? WHERE id=?")) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now())); ps.setInt(2, battleId);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceBattle.startBattle: " + e.getMessage()); }
    }

    public void finishBattle(int battleId) {
        try (PreparedStatement ps = cnx().prepareStatement(
                "UPDATE battle SET status='finished', end_time=? WHERE id=?")) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now())); ps.setInt(2, battleId);
            ps.executeUpdate();
        } catch (SQLException e) { System.err.println("ServiceBattle.finishBattle: " + e.getMessage()); }
    }

    public List<Battle> getAll() {
        List<Battle> list = new ArrayList<>();
        try (Statement st = cnx().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM battle")) {
            while (rs.next()) {
                Battle b = new Battle(rs.getInt("id"), rs.getString("battle_type"),
                        rs.getString("status"), null, null, rs.getString("gagnant"));
                Timestamp s = rs.getTimestamp("start_time"); if (s != null) b.setStartTime(s.toLocalDateTime());
                Timestamp e = rs.getTimestamp("end_time");   if (e != null) b.setEndTime(e.toLocalDateTime());
                list.add(b);
            }
        } catch (SQLException e) { System.err.println("ServiceBattle.getAll: " + e.getMessage()); }
        return list;
    }
}
