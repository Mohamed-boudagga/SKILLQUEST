package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Gestionnaire de connexion Singleton pour MySQL.
 * Assure une instance unique de connexion pour toute l'application.
 */
public class Mydb {

    private static Mydb instance;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/workshop";
    private final String user = "root";
    private final String pass = "";

    private Mydb() {
        try {
            // 1. Connexion au serveur MySQL sans spécifier de DB
            String serverUrl = "jdbc:mysql://localhost:3306/";
            Connection serverConn = DriverManager.getConnection(serverUrl, user, pass);
            
            // 2. Création de la base de données si elle n'existe pas
            Statement st = serverConn.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS workshop");
            serverConn.close();
            
            // 3. Connexion à la base de données 'workshop'
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connecté à MySQL (workshop) !");
            creerTables();
        } catch (Exception e) {
            System.err.println("ERREUR CONNEXION DB : " + e.getMessage());
            System.err.println("Assurez-vous que XAMPP/WAMP (MySQL) est bien démarré sur le port 3306.");
        }
    }


    public static Mydb getInstance() {
        if (instance == null) {
            instance = new Mydb();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void creerTables() {
        try {
            Statement st = connection.createStatement();

            // Table des jeux de base
            st.executeUpdate("CREATE TABLE IF NOT EXISTS games (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "TypeJeux VARCHAR(100) NOT NULL, " +
                    "difficulte VARCHAR(50), " +
                    "time_limit INT, " +
                    "ScoreMax INT, " +
                    "description TEXT)");

            // Table des Questions personnalisées
            st.executeUpdate("CREATE TABLE IF NOT EXISTS questions (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "game_id INT, " +
                    "question_text TEXT NOT NULL, " +
                    "opt1 VARCHAR(255), " +
                    "opt2 VARCHAR(255), " +
                    "opt3 VARCHAR(255), " +
                    "correct_answer VARCHAR(255), " +
                    "FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE)");

            // Migration pour ajouter description si elle n'existe pas
            try { st.executeUpdate("ALTER TABLE games ADD COLUMN description TEXT"); } catch (Exception e) {}


            // Table des Battles (Espaces de compétition)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS battle (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "battle_type VARCHAR(50) DEFAULT 'duel', " +
                    "status VARCHAR(50) DEFAULT 'waiting', " +
                    "start_time DATETIME, " +
                    "end_time DATETIME, " +
                    "gagnant VARCHAR(100))");

            // Table des Joueurs (Participants à une battle)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS joueur (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "battle_id INT, " +
                    "user_id INT, " +
                    "score INT DEFAULT 0, " +
                    "rank INT DEFAULT 0, " +
                    "status VARCHAR(50) DEFAULT 'active', " +
                    "FOREIGN KEY (battle_id) REFERENCES battle(id) ON DELETE CASCADE)");

            // Table des corrections de code
            st.executeUpdate("CREATE TABLE IF NOT EXISTS code_corrections (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "game_id INT, " +
                    "instructions TEXT, " +
                    "buggy_code TEXT, " +
                    "correct_code TEXT, " +
                    "FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE)");

            // Table des parties (Historique)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS partie (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "score INT, " +
                    "datee DATE, " +
                    "joueur_id INT, " +
                    "FOREIGN KEY (joueur_id) REFERENCES joueur(id))");

            // Mise à jour de la table battle si elle existait déjà
            try { st.executeUpdate("ALTER TABLE battle ADD COLUMN battle_type VARCHAR(50) DEFAULT 'duel'"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE battle ADD COLUMN status VARCHAR(50) DEFAULT 'waiting'"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE battle ADD COLUMN start_time DATETIME"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE battle ADD COLUMN end_time DATETIME"); } catch (Exception e) {}

            // Mise à jour de la table joueur si elle existait déjà
            try { st.executeUpdate("ALTER TABLE joueur ADD COLUMN battle_id INT"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE joueur ADD COLUMN user_id INT"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE joueur ADD COLUMN score INT DEFAULT 0"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE joueur ADD COLUMN rank INT DEFAULT 0"); } catch (Exception e) {}
            try { st.executeUpdate("ALTER TABLE joueur ADD COLUMN status VARCHAR(50) DEFAULT 'active'"); } catch (Exception e) {}

            System.out.println("Schéma de base de données synchronisé !");

        } catch (Exception e) {
            System.out.println("Erreur migration tables : " + e.getMessage());
        }
    }

}