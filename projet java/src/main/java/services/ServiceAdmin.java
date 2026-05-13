package services;

import interfaces.IService;
import models.Admin;
import utils.MyDataBase;
import utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service CRUD pour les Admins avec support BCrypt.
 */
public class ServiceAdmin implements IService<Admin> {

    private Connection cnx;

    public ServiceAdmin() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Admin a) {
        String req = "INSERT INTO `admin` (`nom`, `prenom`, `email`, `mot_de_passe`) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getEmail());
            
            // Hachage si ce n'est pas déjà un haché BCrypt
            String mdp = a.getMotDePasse();
            if (mdp != null && !mdp.startsWith("$2a$")) {
                mdp = PasswordUtils.hashPassword(mdp);
            }
            ps.setString(4, mdp);
            
            ps.executeUpdate();
            System.out.println("✔ Admin ajouté : " + a.getNom() + " " + a.getPrenom());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur add Admin : " + ex.getMessage());
        }
    }

    @Override
    public List<Admin> getAll() {
        List<Admin> admins = new ArrayList<>();
        String req = "SELECT * FROM `admin`";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs  = stm.executeQuery(req);
            while (rs.next()) {
                admins.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getAll Admin : " + ex.getMessage());
        }
        return admins;
    }

    @Override
    public Admin getById(int id) {
        String req = "SELECT * FROM `admin` WHERE `id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getById Admin : " + ex.getMessage());
        }
        return null;
    }

    public Admin getByEmail(String email) {
        String req = "SELECT * FROM `admin` WHERE `email` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getByEmail Admin : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void update(Admin a) {
        String req = "UPDATE `admin` SET `nom`=?, `prenom`=?, `email`=?, `mot_de_passe`=? WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getEmail());
            
            String mdp = a.getMotDePasse();
            if (mdp != null && !mdp.startsWith("$2a$")) {
                mdp = PasswordUtils.hashPassword(mdp);
            }
            ps.setString(4, mdp);
            
            ps.setInt   (5, a.getId());
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "✔ Admin mis à jour" : "⚠ Aucun admin mis à jour");
        } catch (SQLException ex) {
            System.out.println("✘ Erreur update Admin : " + ex.getMessage());
        }
    }

    @Override
    public void delete(Admin a) {
        String req = "DELETE FROM `admin` WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, a.getId());
            ps.executeUpdate();
            System.out.println("✔ Admin supprimé");
        } catch (SQLException ex) {
            System.out.println("✘ Erreur delete Admin : " + ex.getMessage());
        }
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        return new Admin(
                rs.getInt   ("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe")
        );
    }
}
