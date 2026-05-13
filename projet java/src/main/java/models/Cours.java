package models;

/**
 * Modèle Cours — un cours ajouté par l'admin dans SkillQuest.
 */
public class Cours {

    private int    id;
    private String titre;
    private String description;
    private int    niveauRequis;   // niveau minimum pour accéder au cours
    private int    adminId;        // l'admin qui a créé ce cours


    // Constructeurs


    public Cours() {}

    /** Pour INSERT (sans id) */
    public Cours(String titre, String description, int niveauRequis, int adminId) {
        this.titre        = titre;
        this.description  = description;
        this.niveauRequis = niveauRequis;
        this.adminId      = adminId;
    }

    /** Pour SELECT (avec id) */
    public Cours(int id, String titre, String description, int niveauRequis, int adminId) {
        this.id           = id;
        this.titre        = titre;
        this.description  = description;
        this.niveauRequis = niveauRequis;
        this.adminId      = adminId;
    }

    // ----------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getTitre()                    { return titre; }
    public void setTitre(String titre)          { this.titre = titre; }

    public String getDescription()              { return description; }
    public void setDescription(String desc)     { this.description = desc; }

    public int getNiveauRequis()                { return niveauRequis; }
    public void setNiveauRequis(int n)          { this.niveauRequis = n; }

    public int getAdminId()                     { return adminId; }
    public void setAdminId(int adminId)         { this.adminId = adminId; }

    // ----------------------------------------------------------------
    // toString
    // ----------------------------------------------------------------

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", niveauRequis=" + niveauRequis +
                ", adminId=" + adminId +
                '}';
    }
}
