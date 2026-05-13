package services;

import models.Badge;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceBadge {

    private Connection cnx;

    public ServiceBadge() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public List<Badge> getBadgesEtudiant(int etudiantId) {
        List<Badge> list = new ArrayList<>();
        String req = "SELECT b.* FROM badge b " +
                     "JOIN etudiant_badge eb ON b.id = eb.badge_id " +
                     "WHERE eb.etudiant_id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Badge(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("icone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getBadges : " + e.getMessage());
        }
        return list;
    }

    public void debloquerBadge(int etudiantId, int badgeId) {
        String req = "INSERT IGNORE INTO etudiant_badge (etudiant_id, badge_id) VALUES (?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, etudiantId);
            ps.setInt(2, badgeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur debloquerBadge : " + e.getMessage());
        }
    }
}
