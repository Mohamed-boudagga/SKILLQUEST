package models;

public class Etudiant {

    private int    id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private int    niveau;
    private int    points;
    private boolean estMentor;
    private String telephone;   // NOUVEAU
    private String sexe;        // NOUVEAU  ("M" ou "F")
    private boolean estBloque;  // NOUVEAU  (controle admin)
    private String photoProfil; // Chemin vers la photo de profil

    // ----------------------------------------------------------------
    // Constructeurs
    // ----------------------------------------------------------------

    public Etudiant() {}

    /** Constructeur complet sans id (pour INSERT) */
    public Etudiant(String nom, String prenom, String email,
                    String motDePasse, int niveau, int points,
                    boolean estMentor, String telephone, String sexe) {
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.motDePasse = motDePasse;
        this.niveau     = niveau;
        this.points     = points;
        this.estMentor  = estMentor;
        this.telephone  = telephone;
        this.sexe       = sexe;
        this.estBloque  = false; // par défaut non bloqué
    }

    /** Constructeur complet avec id (pour SELECT) */
    public Etudiant(int id, String nom, String prenom, String email,
                    String motDePasse, int niveau, int points,
                    boolean estMentor, String telephone, String sexe, boolean estBloque) {
        this.id         = id;
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.motDePasse = motDePasse;
        this.niveau     = niveau;
        this.points     = points;
        this.estMentor  = estMentor;
        this.telephone  = telephone;
        this.sexe       = sexe;
        this.estBloque  = estBloque;
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public String getNom()                  { return nom; }
    public void setNom(String nom)          { this.nom = nom; }

    public String getPrenom()               { return prenom; }
    public void setPrenom(String prenom)    { this.prenom = prenom; }

    public String getEmail()                { return email; }
    public void setEmail(String email)      { this.email = email; }

    public String getMotDePasse()                        { return motDePasse; }
    public void setMotDePasse(String motDePasse)         { this.motDePasse = motDePasse; }

    public int getNiveau()                  { return niveau; }
    public void setNiveau(int niveau)       { this.niveau = niveau; }

    public int getPoints()                  { return points; }
    public void setPoints(int points)       { this.points = points; }

    public boolean isEstMentor()                         { return estMentor; }
    public void setEstMentor(boolean estMentor)          { this.estMentor = estMentor; }

    public String getTelephone()                         { return telephone; }
    public void setTelephone(String telephone)           { this.telephone = telephone; }

    public String getSexe()                              { return sexe; }
    public void setSexe(String sexe)                     { this.sexe = sexe; }

    public boolean isEstBloque()                         { return estBloque; }
    public void setEstBloque(boolean estBloque)          { this.estBloque = estBloque; }

    public String getPhotoProfil()                       { return photoProfil; }
    public void setPhotoProfil(String photoProfil)       { this.photoProfil = photoProfil; }

    // ----------------------------------------------------------------
    // toString
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return "Etudiant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", sexe='" + sexe + '\'' +
                ", niveau=" + niveau +
                ", points=" + points +
                ", estMentor=" + estMentor +
                ", estBloque=" + estBloque +
                "}\n";
    }
}