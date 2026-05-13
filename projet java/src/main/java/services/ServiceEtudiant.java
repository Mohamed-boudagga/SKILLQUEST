package services;

import interfaces.IService;
import models.Etudiant;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import utils.PasswordUtils;

/**
 * Service CRUD pour les Etudiants.
 * Inclut également les opérations de blocage/déblocage (réservées à l'Admin).
 */
public class ServiceEtudiant implements IService<Etudiant> {

    private Connection cnx;

    public ServiceEtudiant() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    // ----------------------------------------------------------------
    // ADD
    // ----------------------------------------------------------------
    @Override
    public void add(Etudiant e) {
        String req = "INSERT INTO `etudiant` "
                + "(`nom`, `prenom`, `email`, `mot_de_passe`, `niveau`, `points`, `est_mentor`, `telephone`, `sexe`, `est_bloque`) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString (1, e.getNom());
            ps.setString (2, e.getPrenom());
            ps.setString (3, e.getEmail());
            
            // Hachage automatique si nécessaire
            String mdp = e.getMotDePasse();
            if (mdp != null && !mdp.startsWith("$2a$")) {
                mdp = PasswordUtils.hashPassword(mdp);
            }
            ps.setString (4, mdp);
            
            ps.setInt    (5, e.getNiveau());
            ps.setInt    (6, e.getPoints());
            ps.setBoolean(7, e.isEstMentor());
            ps.setString (8, e.getTelephone());
            ps.setString (9, e.getSexe());
            ps.setBoolean(10, e.isEstBloque());
            ps.executeUpdate();
            System.out.println("✔ Etudiant ajouté : " + e.getNom() + " " + e.getPrenom());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur add : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // VERIFIER UNICITE EMAIL
    // ----------------------------------------------------------------
    public boolean emailExiste(String email) {
        String req = "SELECT COUNT(*) FROM `etudiant` WHERE `email` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            System.out.println("✘ Erreur emailExiste : " + ex.getMessage());
        }
        return false;
    }

    public boolean emailExistePourAutre(String email, int excludeId) {
        String req = "SELECT COUNT(*) FROM `etudiant` WHERE `email` = ? AND `id` != ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            System.out.println("✘ Erreur emailExistePourAutre : " + ex.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------------
    @Override
    public List<Etudiant> getAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String req = "SELECT * FROM `etudiant`";
        try {
            Statement  stm = cnx.createStatement();
            ResultSet  rs  = stm.executeQuery(req);
            while (rs.next()) {
                etudiants.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getAll : " + ex.getMessage());
        }
        return etudiants;
    }

    // ----------------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------------
    @Override
    public Etudiant getById(int id) {
        String req = "SELECT * FROM `etudiant` WHERE `id` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getById : " + ex.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    // GET BY EMAIL (utile pour la connexion)
    // ----------------------------------------------------------------
    public Etudiant getByEmail(String email) {
        String req = "SELECT * FROM `etudiant` WHERE `email` = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getByEmail : " + ex.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @Override
    public void update(Etudiant e) {
        String req = "UPDATE `etudiant` SET "
                + "`nom`=?, `prenom`=?, `email`=?, `mot_de_passe`=?, "
                + "`niveau`=?, `points`=?, `est_mentor`=?, "
                + "`telephone`=?, `sexe`=?, `est_bloque`=?, `photo_profil`=? "
                + "WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString (1,  e.getNom());
            ps.setString (2,  e.getPrenom());
            ps.setString (3,  e.getEmail());
            
            // Hachage automatique si nécessaire
            String mdp = e.getMotDePasse();
            if (mdp != null && !mdp.startsWith("$2a$")) {
                mdp = PasswordUtils.hashPassword(mdp);
            }
            ps.setString (4,  mdp);
            
            ps.setInt    (5,  e.getNiveau());
            ps.setInt    (6,  e.getPoints());
            ps.setBoolean(7,  e.isEstMentor());
            ps.setString (8,  e.getTelephone());
            ps.setString (9,  e.getSexe());
            ps.setBoolean(10, e.isEstBloque());
            ps.setString (11, e.getPhotoProfil());
            ps.setInt    (12, e.getId());
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                    ? "✔ Etudiant mis à jour (id=" + e.getId() + ")"
                    : "⚠ Aucun étudiant avec id=" + e.getId());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur update : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    @Override
    public void delete(Etudiant e) {
        String req = "DELETE FROM `etudiant` WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, e.getId());
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                    ? "✔ Etudiant supprimé (id=" + e.getId() + ")"
                    : "⚠ Aucun étudiant avec id=" + e.getId());
        } catch (SQLException ex) {
            System.out.println("✘ Erreur delete : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // BLOQUER / DEBLOQUER  (action réservée à l'Admin)
    // ----------------------------------------------------------------

    /** Bloque un étudiant et envoie une notification par email. */
    public void bloquer(int etudiantId) {
        Etudiant et = getById(etudiantId);
        if (et != null) {
            setBloque(etudiantId, true);
            String sujet = "SkillQuest - Compte Bloqué";
            String msg = "Bonjour " + et.getPrenom() + ",\n\n"
                    + "Nous vous informons que votre compte SkillQuest a été suspendu par l'administration.\n"
                    + "Si vous pensez qu'il s'agit d'une erreur, veuillez nous contacter.\n\n"
                    + "Cordialement,\nL'équipe SkillQuest";
            utils.EmailService.envoyerEmail(et.getEmail(), sujet, msg);
            System.out.println("🔒 Etudiant bloqué et notifié : " + et.getEmail());
        }
    }

    /** Débloque un étudiant et envoie une notification par email. */
    public void debloquer(int etudiantId) {
        Etudiant et = getById(etudiantId);
        if (et != null) {
            setBloque(etudiantId, false);
            String sujet = "SkillQuest - Compte Réactivé";
            String msg = "Bonjour " + et.getPrenom() + ",\n\n"
                    + "Bonne nouvelle ! Votre compte SkillQuest a été débloqué.\n"
                    + "Vous pouvez dès à présent vous reconnecter à la plateforme.\n\n"
                    + "Cordialement,\nL'équipe SkillQuest";
            utils.EmailService.envoyerEmail(et.getEmail(), sujet, msg);
            System.out.println("🔓 Etudiant débloqué et notifié : " + et.getEmail());
        }
    }

    private void setBloque(int etudiantId, boolean bloque) {
        String req = "UPDATE `etudiant` SET `est_bloque`=? WHERE `id`=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setBoolean(1, bloque);
            ps.setInt    (2, etudiantId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("✘ Erreur setBloque : " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Méthode utilitaire de mapping ResultSet -> Etudiant
    // ----------------------------------------------------------------
    public List<Etudiant> getClassement() {
        List<Etudiant> list = new ArrayList<>();
        String req = "SELECT * FROM `etudiant` ORDER BY `points` DESC LIMIT 50";
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs  = stm.executeQuery(req);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("✘ Erreur getClassement : " + ex.getMessage());
        }
        return list;
    }

    private Etudiant mapRow(ResultSet rs) throws SQLException {
        Etudiant e = new Etudiant();
        e.setId         (rs.getInt    ("id"));
        e.setNom        (rs.getString ("nom"));
        e.setPrenom     (rs.getString ("prenom"));
        e.setEmail      (rs.getString ("email"));
        e.setMotDePasse (rs.getString ("mot_de_passe"));
        e.setNiveau     (rs.getInt    ("niveau"));
        e.setPoints     (rs.getInt    ("points"));
        e.setEstMentor  (rs.getBoolean("est_mentor"));
        e.setTelephone  (rs.getString ("telephone"));
        e.setSexe       (rs.getString ("sexe"));
        e.setEstBloque  (rs.getBoolean("est_bloque"));
        try { e.setPhotoProfil(rs.getString("photo_profil")); } catch (SQLException ignored) {}
        return e;
    }
}
