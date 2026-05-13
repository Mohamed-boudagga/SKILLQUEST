package utils;

import models.Admin;
import models.Etudiant;
import services.ServiceAdmin;
import services.ServiceEtudiant;
import java.util.List;

/**
 * Utilitaire pour migrer les mots de passe existants en clair vers le format haché BCrypt.
 * À exécuter UNE SEULE FOIS.
 */
public class PasswordMigration {

    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de la migration des mots de passe...");

        ServiceEtudiant serviceEtudiant = new ServiceEtudiant();
        ServiceAdmin serviceAdmin = new ServiceAdmin();

        // 1. Migration des Étudiants
        List<Etudiant> etudiants = serviceEtudiant.getAll();
        int countEtud = 0;
        for (Etudiant e : etudiants) {
            String mdp = e.getMotDePasse();
            // Si le mot de passe ne commence pas par le préfixe BCrypt standard
            if (mdp != null && !mdp.startsWith("$2a$")) {
                e.setMotDePasse(PasswordUtils.hashPassword(mdp));
                serviceEtudiant.update(e);
                countEtud++;
            }
        }
        System.out.println("✅ Étudiants migrés : " + countEtud);

        // 2. Migration des Admins
        List<Admin> admins = serviceAdmin.getAll();
        int countAdmin = 0;
        for (Admin a : admins) {
            String mdp = a.getMotDePasse();
            if (mdp != null && !mdp.startsWith("$2a$")) {
                a.setMotDePasse(PasswordUtils.hashPassword(mdp));
                serviceAdmin.update(a);
                countAdmin++;
            }
        }
        System.out.println("✅ Admins migrés : " + countAdmin);

        System.out.println("🏁 Migration terminée avec succès !");
        System.exit(0);
    }
}
