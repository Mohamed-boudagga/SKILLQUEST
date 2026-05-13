import models.Admin;
import models.Cours;
import models.Etudiant;
import services.ServiceAdmin;
import services.ServiceCours;
import services.ServiceEtudiant;
import utils.MyDataBase;
import utils.PasswordUtils;

import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        banner("SkillQuest v2 — Module Gestion Utilisateurs");

        if (!MyDataBase.getInstance().isConnected()) {
            System.err.println("Impossible de continuer sans connexion à la base de données.");
            return;
        }

        // Nettoyage des tables pour un test propre
        try {
            Statement stmt = MyDataBase.getInstance().getCnx().createStatement();
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
            stmt.executeUpdate("TRUNCATE TABLE cours");
            stmt.executeUpdate("TRUNCATE TABLE etudiant");
            stmt.executeUpdate("TRUNCATE TABLE admin");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
            System.out.println("✔ Tables réinitialisées.\n");
        } catch (SQLException e) {
            System.out.println("Erreur nettoyage : " + e.getMessage());
        }

        // MODULE ADMIN — CRUD

        banner("MODULE ADMIN");
        ServiceAdmin serviceAdmin = new ServiceAdmin();

        serviceAdmin.add(new Admin("Admin",    "Principal", "admin@skillquest.tn", "admin123"));
        serviceAdmin.add(new Admin("Directeur","Sami",      "sami@skillquest.tn",  "sami2024"));

        System.out.println("\n--- Tous les admins ---");
        serviceAdmin.getAll().forEach(System.out::println);

        // Test login admin
        System.out.println("\n--- Login Admin ---");
        Admin adminConnecte = serviceAdmin.getByEmail("admin@skillquest.tn");
        if (adminConnecte != null && PasswordUtils.checkPassword("admin123", adminConnecte.getMotDePasse())) {
            System.out.println("Connecté : " + adminConnecte);
        } else {
            System.out.println("Connexion échouée");
        }

        // MODULE ETUDIANT — CRUD avec telephone & sexe

        banner("MODULE ETUDIANT");
        ServiceEtudiant serviceEtudiant = new ServiceEtudiant();

        serviceEtudiant.add(new Etudiant("Ben Ali",  "Ahmed",  "ahmed@esprit.tn",  "pass123",       1,   0,   false, "20123456", "M"));
        serviceEtudiant.add(new Etudiant("Trabelsi", "Sarra",  "sarra@esprit.tn",  "pass456",       2, 150,   false, "22334455", "F"));
        serviceEtudiant.add(new Etudiant("Mansouri", "Khalil", "khalil@esprit.tn", "pass789",       3, 500,   true,  "55667788", "M"));
        serviceEtudiant.add(new Etudiant("Briki",    "Oussama","oussama@esprit.tn","pass1234",      1,   0,   false, "98765432", "M"));
        serviceEtudiant.add(new Etudiant("Boudagga", "Mohamed","mohamed@esprit.tn","pass12345678",  5,1000,   true,  "27182818", "M"));

        System.out.println("\n--- Tous les étudiants ---");
        serviceEtudiant.getAll().forEach(System.out::println);

        // Update
        System.out.println("--- UPDATE étudiant id=1 ---");
        Etudiant e1 = serviceEtudiant.getById(1);
        if (e1 != null) {
            e1.setPoints(300);
            e1.setNiveau(2);
            e1.setTelephone("20999999");
            serviceEtudiant.update(e1);
        }

        // Bloquer / Débloquer (action Admin)
        System.out.println("\n--- BLOCAGE/DEBLOCAGE (action Admin) ---");
        serviceEtudiant.bloquer(2);    // Sarra bloquée
        serviceEtudiant.bloquer(4);    // Briki bloqué
        serviceEtudiant.debloquer(2);  // Sarra débloquée

        System.out.println("\n--- Etudiants après blocage/déblocage ---");
        serviceEtudiant.getAll().forEach(System.out::println);

        // MODULE COURS — CRUD (géré par Admin)

        banner("MODULE COURS (géré par Admin)");
        ServiceCours serviceCours = new ServiceCours();

        int adminId = adminConnecte != null ? adminConnecte.getId() : 1;

        serviceCours.add(new Cours("Java Débutant",      "Introduction à Java.",              1, adminId));
        serviceCours.add(new Cours("Java Avancé",        "Streams, Lambdas, Génériques.",     3, adminId));
        serviceCours.add(new Cours("Base de Données SQL","Requêtes SQL avec MySQL.",          2, adminId));
        serviceCours.add(new Cours("Design Patterns",    "Patrons de conception GoF.",        4, adminId));
        serviceCours.add(new Cours("Développement Web",  "HTML, CSS, JS et frameworks.",      2, adminId));

        System.out.println("\n--- Tous les cours ---");
        serviceCours.getAll().forEach(System.out::println);

        // Cours accessibles à un étudiant de niveau 2
        System.out.println("\n--- Cours accessibles à un étudiant niveau 2 ---");
        serviceCours.getCoursAccessibles(2).forEach(System.out::println);

        // Update cours
        System.out.println("--- UPDATE cours id=1 ---");
        Cours c1 = serviceCours.getById(1);
        if (c1 != null) {
            c1.setTitre("Java Débutant — Edition 2026");
            serviceCours.update(c1);
        }

        // Delete cours
        System.out.println("--- DELETE cours id=1 ---");
        Cours cDel = serviceCours.getById(1);
        if (cDel != null) serviceCours.delete(cDel);

        System.out.println("\n--- Cours après suppression ---");
        serviceCours.getAll().forEach(System.out::println);


        banner("Tous les tests CRUD terminés avec succès !");
    }

    private static void banner(String title) {
        String line = "=".repeat(55);
        System.out.println("\n" + line);
        System.out.printf("  %s%n", title);
        System.out.println(line);
    }
}