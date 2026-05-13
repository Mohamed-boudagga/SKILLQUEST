package services;

import models.ProgressionCours;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProgression {

    private Connection cnx;

    public ServiceProgression() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public void upsertProgression(ProgressionCours p) {
        String req = "INSERT INTO progression_cours (etudiant_id, cours_id, pourcentage, statut) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE pourcentage = ?, statut = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, p.getEtudiantId());
            ps.setInt(2, p.getCoursId());
            ps.setInt(3, p.getPourcentage());
            ps.setString(4, p.getStatut());
            ps.setInt(5, p.getPourcentage());
            ps.setString(6, p.getStatut());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur upsertProgression : " + e.getMessage());
        }
    }

    public List<ProgressionCours> getProgressionByEtudiant(int etudiantId) {
        List<ProgressionCours> list = new ArrayList<>();
        String req = "SELECT pc.*, c.titre FROM progression_cours pc " +
                     "JOIN cours c ON pc.cours_id = c.id " +
                     "WHERE pc.etudiant_id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProgressionCours p = new ProgressionCours(
                        rs.getInt("etudiant_id"),
                        rs.getInt("cours_id"),
                        rs.getInt("pourcentage"),
                        rs.getString("statut")
                );
                p.setTitreCours(rs.getString("titre"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getProgression : " + e.getMessage());
        }
        return list;
    }
}
