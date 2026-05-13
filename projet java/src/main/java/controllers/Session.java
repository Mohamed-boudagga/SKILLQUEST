package controllers;

import models.Admin;
import models.Etudiant;

/**
 * Session statique — stocke l'utilisateur connecté.
 * Permet aux controllers de partager l'état courant.
 */
public class Session {

    private static Admin    adminConnecte    = null;
    private static Etudiant etudiantConnecte = null;

    // Getters / Setters
    public static Admin    getAdminConnecte()             { return adminConnecte; }
    public static void     setAdminConnecte(Admin a)      { adminConnecte = a; etudiantConnecte = null; }

    public static Etudiant getEtudiantConnecte()          { return etudiantConnecte; }
    public static void     setEtudiantConnecte(Etudiant e){ etudiantConnecte = e; adminConnecte = null; }

    /** Efface la session (déconnexion). */
    public static void vider() {
        adminConnecte    = null;
        etudiantConnecte = null;
    }

    public static boolean estAdmin()    { return adminConnecte != null; }
    public static boolean estEtudiant() { return etudiantConnecte != null; }
}
