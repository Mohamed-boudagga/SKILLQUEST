package models;

/**
 * Modèle Admin — utilisateur administrateur de SkillQuest.
 * L'admin peut : gérer les étudiants, ajouter/modifier des cours,
 * bloquer ou débloquer un étudiant.
 */
public class Admin {

    private int    id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;


    // Constructeurs


    public Admin() {}

    /** Pour INSERT (sans id) */
    public Admin(String nom, String prenom, String email, String motDePasse) {
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.motDePasse = motDePasse;
    }

    /** Pour SELECT (avec id) */
    public Admin(int id, String nom, String prenom, String email, String motDePasse) {
        this.id         = id;
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.motDePasse = motDePasse;
    }


    // Getters / Setters


    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getNom()                      { return nom; }
    public void setNom(String nom)              { this.nom = nom; }

    public String getPrenom()                   { return prenom; }
    public void setPrenom(String prenom)        { this.prenom = prenom; }

    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }

    public String getMotDePasse()               { return motDePasse; }
    public void setMotDePasse(String motDePasse){ this.motDePasse = motDePasse; }


    // toString


    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
