package services;

import interfaces.IService;
import models.Cours;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service CRUD pour les Cours.
 * Seul l'Admin peut ajouter / modifier / supprimer des cours.
 */
public class ServiceCours implements IService<Cours> {

    private Connection cnx;

    public ServiceCours() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    // ----------------------------------------------------------------
    // ADD
    // ----------------------------------------------------------------
    @Override
    public void add(Cours c) {
        String req = "INSERT INTO `cours` (`titre`, `description`, `niveau_requis`, `admin_id`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, c.getTitre());
            ps.setString(2, c.getDescription());
            ps.setInt   (3, c.getNiveauRequis());
            ps.setInt   (4, c.getAdminId());
            ps.executeUpdate();
            System.out.println("✔ Cours ajouté : " + c.getTitre());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur add Cours : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------------
    @Override
    public List<Cours> getAll() {
        List<Cours> liste = new ArrayList<>();
        String req = "SELECT * FROM `cours`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs  = stm.executeQuery(req);
            while (rs.next()) {
                liste.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getAll Cours : " + ex.getMessage());
        }
        return liste;
    }

    // ----------------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------------
    @Override
    public Cours getById(int id) {
        String req = "SELECT * FROM `cours` WHERE `id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getById Cours : " + ex.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    // GET COURS PAR ADMIN
    // ----------------------------------------------------------------
    public List<Cours> getByAdmin(int adminId) {
        List<Cours> liste = new ArrayList<>();
        String req = "SELECT * FROM `cours` WHERE `admin_id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapRow(rs));
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getByAdmin : " + ex.getMessage());
        }
        return liste;
    }

    // ----------------------------------------------------------------
    // GET COURS ACCESSIBLES A UN ETUDIANT (par niveau)
    // ----------------------------------------------------------------
    public List<Cours> getCoursAccessibles(int niveauEtudiant) {
        List<Cours> liste = new ArrayList<>();
        String req = "SELECT * FROM `cours` WHERE `niveau_requis` <= ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, niveauEtudiant);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapRow(rs));
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getCoursAccessibles : " + ex.getMessage());
        }
        return liste;
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @Override
    public void update(Cours c) {
        String req = "UPDATE `cours` SET `titre`=?, `description`=?, `niveau_requis`=?, `admin_id`=? WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, c.getTitre());
            ps.setString(2, c.getDescription());
            ps.setInt   (3, c.getNiveauRequis());
            ps.setInt   (4, c.getAdminId());
            ps.setInt   (5, c.getId());
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                    ? "✔ Cours mis à jour (id=" + c.getId() + ")"
                    : "⚠ Aucun cours avec id=" + c.getId());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur update Cours : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    @Override
    public void delete(Cours c) {
        String req = "DELETE FROM `cours` WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, c.getId());
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                    ? "✔ Cours supprimé (id=" + c.getId() + ")"
                    : "⚠ Aucun cours avec id=" + c.getId());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur delete Cours : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Mapping ResultSet -> Cours
    // ----------------------------------------------------------------
    private Cours mapRow(ResultSet rs) throws SQLException {
        return new Cours(
                rs.getInt   ("id"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getInt   ("niveau_requis"),
                rs.getInt   ("admin_id")
        );
    }
}
