package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDataBase {

    private static MyDataBase instance;

    //  CONFIGURATION WAMP

    private static final String HOST     = "127.0.0.1";
    private static final String PORT     = "3306";
    private static final String DB_NAME  = "skillquest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";   // vide par defaut WAMP

    private static final String URL_NO_DB = "jdbc:mysql://" + HOST + ":" + PORT
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private Connection cnx;

    private MyDataBase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Etape 1 : creer la base si elle n'existe pas
            createDatabaseIfNotExists();

            // Etape 2 : connexion a la base
            this.cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println(" Connexion WAMP/MySQL reussie !");

            // Etape 3 : creer les tables si elles n'existent pas
            createTablesIfNotExist();

            // Etape 4 : migration automatique des colonnes manquantes
            migrateColumnsIfNeeded();

        } catch (ClassNotFoundException e) {
            System.out.println(" Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println(" Erreur connexion : " + e.getMessage());
            System.out.println("   Verifiez que WAMP est demarre.");
        }
    }


    // Crée la base de données si absente

    private void createDatabaseIfNotExists() throws SQLException {
        try (Connection tmp = DriverManager.getConnection(URL_NO_DB, USERNAME, PASSWORD);
             Statement  st  = tmp.createStatement()) {
            st.executeUpdate(
                "CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` " +
                "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println(" Base de donnees '" + DB_NAME + "' verifiee/creee.");
        }
    }

    // Crée les 3 tables (admin, etudiant, cours) si elles n'existent pas

    private void createTablesIfNotExist() {
        try (Statement st = cnx.createStatement()) {

            // TABLE admin
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `admin` (" +
                "  `id`           INT          NOT NULL AUTO_INCREMENT," +
                "  `nom`          VARCHAR(100) NOT NULL," +
                "  `prenom`       VARCHAR(100) NOT NULL," +
                "  `email`        VARCHAR(150) NOT NULL UNIQUE," +
                "  `mot_de_passe` VARCHAR(255) NOT NULL," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'admin' verifiee/creee.");

            // TABLE etudiant (schema complet v2)
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `etudiant` (" +
                "  `id`           INT           NOT NULL AUTO_INCREMENT," +
                "  `nom`          VARCHAR(100)  NOT NULL," +
                "  `prenom`       VARCHAR(100)  NOT NULL," +
                "  `email`        VARCHAR(150)  NOT NULL UNIQUE," +
                "  `mot_de_passe` VARCHAR(255)  NOT NULL," +
                "  `niveau`       INT           NOT NULL DEFAULT 1," +
                "  `points`       INT           NOT NULL DEFAULT 0," +
                "  `est_mentor`   TINYINT(1)    NOT NULL DEFAULT 0," +
                "  `telephone`    VARCHAR(20)   DEFAULT NULL," +
                "  `sexe`         ENUM('M','F') DEFAULT NULL," +
                "  `est_bloque`   TINYINT(1)    NOT NULL DEFAULT 0," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'etudiant' verifiee/creee.");

            // TABLE cours
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `cours` (" +
                "  `id`            INT          NOT NULL AUTO_INCREMENT," +
                "  `titre`         VARCHAR(200) NOT NULL," +
                "  `description`   TEXT," +
                "  `niveau_requis` INT          NOT NULL DEFAULT 1," +
                "  `admin_id`      INT          NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_cours_admin`" +
                "    FOREIGN KEY (`admin_id`) REFERENCES `admin`(`id`)" +
                "    ON DELETE CASCADE ON UPDATE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'cours' verifiee/creee.");

            // TABLE test
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `test` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `titre`       VARCHAR(200) NOT NULL," +
                "  `score_min`   INT          NOT NULL DEFAULT 50," +
                "  `cours_id`    INT          NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_test_cours`" +
                "    FOREIGN KEY (`cours_id`) REFERENCES `cours`(`id`)" +
                "    ON DELETE CASCADE ON UPDATE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'test' verifiee/creee.");

            // TABLE certificat
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `certificat` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `nom`         VARCHAR(200) NOT NULL," +
                "  `test_id`     INT          NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_certif_test`" +
                "    FOREIGN KEY (`test_id`) REFERENCES `test`(`id`)" +
                "    ON DELETE CASCADE ON UPDATE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'certificat' verifiee/creee.");

            // TABLE jeu
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `jeu` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `nom`         VARCHAR(200) NOT NULL," +
                "  `type`        ENUM('BATTLE', 'QUIZ') NOT NULL," +
                "  `description` TEXT," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'jeu' verifiee/creee.");

            // TABLE badge
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `badge` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `nom`         VARCHAR(100) NOT NULL," +
                "  `description` TEXT," +
                "  `icone`       VARCHAR(255)," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'badge' verifiee/creee.");

            // TABLE etudiant_badge (liaison Many-to-Many)
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `etudiant_badge` (" +
                "  `etudiant_id` INT NOT NULL," +
                "  `badge_id`    INT NOT NULL," +
                "  `date_obtention` TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  PRIMARY KEY (`etudiant_id`, `badge_id`)," +
                "  CONSTRAINT `fk_eb_etudiant` FOREIGN KEY (`etudiant_id`) REFERENCES `etudiant`(`id`) ON DELETE CASCADE," +
                "  CONSTRAINT `fk_eb_badge` FOREIGN KEY (`badge_id`) REFERENCES `badge`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'etudiant_badge' verifiee/creee.");

            // TABLE progression_cours
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `progression_cours` (" +
                "  `etudiant_id` INT NOT NULL," +
                "  `cours_id`    INT NOT NULL," +
                "  `pourcentage` INT DEFAULT 0," +
                "  `statut`      ENUM('EN_COURS', 'TERMINE') DEFAULT 'EN_COURS'," +
                "  PRIMARY KEY (`etudiant_id`, `cours_id`)," +
                "  CONSTRAINT `fk_pc_etudiant` FOREIGN KEY (`etudiant_id`) REFERENCES `etudiant`(`id`) ON DELETE CASCADE," +
                "  CONSTRAINT `fk_pc_cours` FOREIGN KEY (`cours_id`) REFERENCES `cours`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'progression_cours' verifiee/creee.");

            // ── MODULE JEUX ───────────────────────────────────────────
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `games` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `TypeJeux`    VARCHAR(100) NOT NULL," +
                "  `difficulte`  VARCHAR(50)," +
                "  `time_limit`  INT," +
                "  `ScoreMax`    INT," +
                "  `description` TEXT," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'games' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `quiz_questions` (" +
                "  `id`             INT  NOT NULL AUTO_INCREMENT," +
                "  `game_id`        INT," +
                "  `question_text`  TEXT NOT NULL," +
                "  `opt1`           VARCHAR(255)," +
                "  `opt2`           VARCHAR(255)," +
                "  `opt3`           VARCHAR(255)," +
                "  `correct_answer` VARCHAR(255)," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_qq_game` FOREIGN KEY (`game_id`) REFERENCES `games`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'quiz_questions' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `battle` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `battle_type` VARCHAR(50)  DEFAULT 'duel'," +
                "  `status`      VARCHAR(50)  DEFAULT 'waiting'," +
                "  `start_time`  DATETIME," +
                "  `end_time`    DATETIME," +
                "  `gagnant`     VARCHAR(100)," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'battle' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `joueur` (" +
                "  `id`        INT NOT NULL AUTO_INCREMENT," +
                "  `battle_id` INT," +
                "  `user_id`   INT," +
                "  `score`     INT DEFAULT 0," +
                "  `rank`      INT DEFAULT 0," +
                "  `status`    VARCHAR(50) DEFAULT 'active'," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_joueur_battle` FOREIGN KEY (`battle_id`) REFERENCES `battle`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'joueur' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `code_corrections` (" +
                "  `id`           INT  NOT NULL AUTO_INCREMENT," +
                "  `game_id`      INT," +
                "  `instructions` TEXT," +
                "  `buggy_code`   TEXT," +
                "  `correct_code` TEXT," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_cc_game` FOREIGN KEY (`game_id`) REFERENCES `games`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'code_corrections' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `partie` (" +
                "  `id`        INT  NOT NULL AUTO_INCREMENT," +
                "  `score`     INT," +
                "  `datee`     DATE," +
                "  `joueur_id` INT," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_partie_joueur` FOREIGN KEY (`joueur_id`) REFERENCES `joueur`(`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'partie' verifiee/creee.");

            // ── MODULE TEST / CERTIFICATION ───────────────────────────
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `exam` (" +
                "  `id`           INT          NOT NULL AUTO_INCREMENT," +
                "  `nom`          VARCHAR(200) NOT NULL," +
                "  `level`        INT          NOT NULL DEFAULT 1," +
                "  `dureeMinutes` INT          NOT NULL DEFAULT 30," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'exam' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `exam_question` (" +
                "  `id`                   INT  NOT NULL AUTO_INCREMENT," +
                "  `exam_id`              INT  NOT NULL," +
                "  `text`                 TEXT NOT NULL," +
                "  `options`              TEXT," +
                "  `correct_option_index` INT  NOT NULL DEFAULT 0," +
                "  `points`              INT  NOT NULL DEFAULT 1," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_eq_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'exam_question' verifiee/creee.");

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `certification` (" +
                "  `id`            INT          NOT NULL AUTO_INCREMENT," +
                "  `title`         VARCHAR(200) NOT NULL," +
                "  `level`         INT          NOT NULL DEFAULT 1," +
                "  `dateObtention` DATE," +
                "  `description`   TEXT," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'certification' verifiee/creee.");

            // ── MODULE COURS (leçons) ─────────────────────────────────
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `lecon` (" +
                "  `id`          INT          NOT NULL AUTO_INCREMENT," +
                "  `titre`       VARCHAR(200) NOT NULL," +
                "  `description` TEXT," +
                "  `idcours`     INT          NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  CONSTRAINT `fk_lecon_cours` FOREIGN KEY (`idcours`) REFERENCES `cours`(`id`) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            System.out.println("OK Table 'lecon' verifiee/creee.");

        } catch (SQLException e) {
            System.out.println("ERREUR creation tables : " + e.getMessage());
        }
    }

    // Migration automatique : ajoute les colonnes manquantes à etudiant
    // Utile si la table existait AVANT la v2 (sans telephone/sexe/est_bloque)

    private void migrateColumnsIfNeeded() {
        // { nom_table, nom_colonne, definition_SQL }
        String[][] colonnes = {
            { "etudiant", "telephone",    "VARCHAR(20) DEFAULT NULL"        },
            { "etudiant", "sexe",         "ENUM('M','F') DEFAULT NULL"      },
            { "etudiant", "est_bloque",   "TINYINT(1) NOT NULL DEFAULT 0"   },
            { "etudiant", "photo_profil", "VARCHAR(500) DEFAULT NULL"        },
            { "cours",    "niveau",         "VARCHAR(20) DEFAULT NULL"       },
            { "cours",    "contenue",       "TEXT NULL"                      },
            { "cours",    "idAjouteur",     "INT DEFAULT NULL"               },
            { "cours",    "dateDeCreation", "DATETIME DEFAULT NULL"          }
        };
        for (String[] c : colonnes) {
            ajouterColonneSiAbsente(c[0], c[1], c[2]);
        }
    }

    /**
     * Vérifie via les métadonnées JDBC si une colonne existe dans la table.
     * Si elle est absente, exécute un ALTER TABLE pour l'ajouter.
     */
    private void ajouterColonneSiAbsente(String table, String colonne, String definition) {
        try {
            ResultSet rs = cnx.getMetaData().getColumns(DB_NAME, null, table, colonne);
            boolean existe = rs.next();
            rs.close();

            if (!existe) {
                try (Statement st = cnx.createStatement()) {
                    st.executeUpdate(
                        "ALTER TABLE `" + table + "` ADD COLUMN `" + colonne + "` " + definition);
                    System.out.println("Migration OK : colonne '" + colonne
                            + "' ajoutee a la table '" + table + "'.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur migration colonne '" + colonne + "' : " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Singleton / accesseurs
    // ----------------------------------------------------------------
    public static MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getCnx() { return cnx; }

    public boolean isConnected() {
        try {
            return cnx != null && !cnx.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
