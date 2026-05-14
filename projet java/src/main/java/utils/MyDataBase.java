package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

            // Etape 5 : données de démonstration
            seedCoursData();

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

    // Insère des données de démonstration dans cours et lecon si vides

    private void seedCoursData() {
        try (Statement st = cnx.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM `cours`");
            rs.next();
            if (rs.getInt(1) > 0) return; // déjà peuplé
        } catch (SQLException e) {
            System.out.println("Seed check failed: " + e.getMessage());
            return;
        }

        String[][] cours = {
            // { titre, description, niveau, contenue }
            {"Introduction à Java",
             "Découvrez les bases du langage Java : syntaxe, types, variables et premières instructions.",
             "1",
             "Java est un langage orienté objet créé par Sun Microsystems. Dans ce cours, vous apprendrez à déclarer des variables, écrire des conditions et des boucles, et compiler votre premier programme."},

            {"Programmation Orientée Objet",
             "Maîtrisez les concepts fondamentaux de la POO : classes, objets, héritage et polymorphisme.",
             "2",
             "La POO permet de modéliser des entités du monde réel sous forme d'objets. Ce cours couvre l'encapsulation, l'héritage, le polymorphisme et les interfaces en Java."},

            {"Structures de Données",
             "Explorez les listes, piles, files, arbres et tables de hachage avec Java Collections.",
             "2",
             "Les structures de données sont essentielles à tout développeur. Nous étudions ArrayList, LinkedList, HashMap, HashSet, Stack et leurs cas d'usage typiques."},

            {"Algorithmes et Complexité",
             "Analysez et concevez des algorithmes efficaces avec notation Big-O.",
             "3",
             "Ce cours présente les algorithmes de tri (bubble, merge, quick), de recherche (binaire, DFS, BFS) et l'analyse de leur complexité temporelle et spatiale."},

            {"Bases de Données SQL",
             "Apprenez à concevoir et interroger des bases de données relationnelles avec MySQL.",
             "3",
             "Nous couvrons le modèle relationnel, la création de tables, les requêtes SELECT/INSERT/UPDATE/DELETE, les jointures, et les transactions ACID."},

            {"Développement Web Java EE",
             "Créez des applications web dynamiques avec Servlets, JSP et REST APIs.",
             "4",
             "Java EE fournit un écosystème complet pour le web : Servlets pour la logique, JSP pour les vues, JAX-RS pour les APIs REST, et JPA pour la persistance."},

            {"Design Patterns",
             "Découvrez les patrons de conception GoF pour écrire un code robuste et maintenable.",
             "4",
             "Les 23 patterns GoF sont répartis en créationnels (Singleton, Factory), structurels (Adapter, Decorator) et comportementaux (Observer, Strategy). Ce cours les applique en Java."},

            {"Sécurité des Applications",
             "Protégez vos applications contre les vulnérabilités OWASP et implémentez l'authentification.",
             "5",
             "Injection SQL, XSS, CSRF, hash de mots de passe avec BCrypt, JWT, OAuth2 — ce cours vous forme aux bonnes pratiques de sécurité applicative."},

            {"Architecture Microservices",
             "Concevez des systèmes distribués scalables avec Spring Boot et Docker.",
             "5",
             "Les microservices décomposent une application monolithique en services indépendants. Nous étudions Spring Boot, REST, Docker, API Gateway et la gestion des pannes."},

            {"Intelligence Artificielle & Java",
             "Intégrez des modèles de machine learning dans vos applications Java.",
             "6",
             "Ce cours avancé couvre Weka, DL4J et les APIs d'IA cloud. Vous implémenterez un classificateur, un réseau de neurones simple, et connecterez une API de traitement du langage naturel."}
        };

        try {
            for (String[] c : cours) {
                PreparedStatement ps = cnx.prepareStatement(
                    "INSERT INTO `cours` (`titre`, `description`, `niveau`, `contenue`, `idAjouteur`, `dateDeCreation`) VALUES (?,?,?,?,?,NOW())",
                    java.sql.Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, c[0]); ps.setString(2, c[1]);
                ps.setString(3, c[2]); ps.setString(4, c[3]);
                ps.setInt(5, 1);
                ps.executeUpdate();
                ResultSet gen = ps.getGeneratedKeys();
                if (gen.next()) seedLecons(gen.getInt(1), c[0]);
            }
            System.out.println("Seed OK : cours et lecons inseres.");
        } catch (SQLException e) {
            System.out.println("Seed erreur : " + e.getMessage());
        }
    }

    private void seedLecons(int coursId, String coursTitre) throws SQLException {
        String[][][] leconsByCours = {
            // Niveau 1 — Introduction à Java
            {{"Variables et Types primitifs", "Déclarer int, double, boolean, char et String. Comprendre la portée et la durée de vie des variables."},
             {"Conditions et Boucles", "Maîtriser if/else, switch, for, while et do-while pour contrôler le flux d'exécution."},
             {"Méthodes et Fonctions", "Définir et appeler des méthodes, passer des paramètres, retourner des valeurs et comprendre la surcharge."},
             {"Tableaux", "Créer et manipuler des tableaux à une et deux dimensions, et utiliser Arrays.sort() et Arrays.fill()."}},

            // Niveau 2 — POO
            {{"Classes et Objets", "Déclarer une classe, créer des objets avec new, utiliser this et comprendre le cycle de vie d'un objet."},
             {"Héritage et super", "Étendre une classe avec extends, appeler super(), et redéfinir des méthodes."},
             {"Polymorphisme", "Comprendre le polymorphisme dynamique, le late binding et l'utilisation des interfaces."},
             {"Encapsulation", "Appliquer les modificateurs private/protected/public et créer des getters/setters."}},

            // Niveau 2 — Structures de Données
            {{"ArrayList et LinkedList", "Comparer ArrayList et LinkedList, ajouter, supprimer, accéder et itérer les éléments."},
             {"HashMap et HashSet", "Stocker des paires clé-valeur avec HashMap et manipuler des ensembles uniques avec HashSet."},
             {"Stack et Queue", "Implémenter des piles LIFO et des files FIFO avec les classes Java appropriées."},
             {"Tri et Recherche", "Utiliser Collections.sort(), Comparator et binarySearch() pour trier et chercher."}},

            // Niveau 3 — Algorithmes
            {{"Notation Big-O", "Analyser la complexité temporelle O(1), O(n), O(log n), O(n²) avec des exemples concrets."},
             {"Algorithmes de Tri", "Implémenter et comparer Bubble Sort, Selection Sort, Merge Sort et Quick Sort."},
             {"Recherche Binaire", "Comprendre et coder la recherche dichotomique sur tableaux triés."},
             {"Graphes et BFS/DFS", "Modéliser des graphes et explorer leurs noeuds avec les algorithmes BFS et DFS."}},

            // Niveau 3 — SQL
            {{"Modèle Relationnel", "Comprendre les tables, colonnes, clés primaires, clés étrangères et contraintes d'intégrité."},
             {"Requêtes SELECT", "Maîtriser SELECT, WHERE, ORDER BY, GROUP BY, HAVING et les fonctions d'agrégation."},
             {"Jointures SQL", "Utiliser INNER JOIN, LEFT JOIN, RIGHT JOIN et FULL JOIN pour combiner plusieurs tables."},
             {"Transactions et Index", "Gérer les transactions ACID avec COMMIT/ROLLBACK et optimiser avec des index."}},

            // Niveau 4 — Java EE
            {{"Servlets HTTP", "Créer des Servlets pour traiter les requêtes GET et POST et générer des réponses dynamiques."},
             {"JSP et JSTL", "Concevoir des vues dynamiques avec JSP, Expression Language et la bibliothèque JSTL."},
             {"REST APIs avec JAX-RS", "Exposer des ressources JSON via des endpoints REST annotés avec @GET, @POST, @Path."},
             {"JPA et Hibernate", "Mapper des objets Java à des tables SQL avec JPA et Hibernate."}},

            // Niveau 4 — Design Patterns
            {{"Patterns Créationnels", "Étudier Singleton, Factory Method, Abstract Factory et Builder avec des exemples Java."},
             {"Patterns Structurels", "Appliquer Adapter, Decorator, Facade et Proxy pour composer des objets flexibles."},
             {"Patterns Comportementaux", "Implémenter Observer, Strategy, Command et Iterator dans des scénarios réels."},
             {"Anti-patterns à éviter", "Identifier les anti-patterns courants : God Object, Spaghetti Code, Magic Numbers."}},

            // Niveau 5 — Sécurité
            {{"OWASP Top 10", "Découvrir les 10 vulnérabilités les plus critiques : injection, XSS, CSRF et mauvaise configuration."},
             {"Hash et Chiffrement", "Utiliser BCrypt pour hasher les mots de passe et AES pour chiffrer des données sensibles."},
             {"JWT et OAuth2", "Implémenter l'authentification sans état avec JSON Web Tokens et déléguer via OAuth2."},
             {"Tests de Sécurité", "Réaliser des tests de pénétration basiques et utiliser OWASP ZAP pour scanner une app."}},

            // Niveau 5 — Microservices
            {{"Spring Boot Fondamentaux", "Créer un projet Spring Boot, configurer application.yml et exposer des endpoints REST."},
             {"Docker et Conteneurisation", "Écrire un Dockerfile, construire une image et orchestrer des conteneurs avec Docker Compose."},
             {"API Gateway et Load Balancing", "Configurer un API Gateway pour router les requêtes et équilibrer la charge."},
             {"Résilience et Circuit Breaker", "Implémenter Hystrix/Resilience4j pour gérer les pannes en cascade."}},

            // Niveau 6 — IA
            {{"Introduction au Machine Learning", "Comprendre la classification, régression et clustering. Utiliser Weka en Java."},
             {"Réseaux de Neurones avec DL4J", "Construire et entraîner un réseau de neurones multicouche avec Deeplearning4j."},
             {"Traitement du Langage Naturel", "Tokenisation, analyse de sentiment et intégration d'une API NLP (OpenNLP, OpenAI)."},
             {"IA dans les Applications Réelles", "Intégrer un modèle IA exporté dans une application Java/JavaFX de bout en bout."}}
        };

        // Map course title to lecon array index
        String[] titres = {
            "Introduction à Java", "Programmation Orientée Objet", "Structures de Données",
            "Algorithmes et Complexité", "Bases de Données SQL", "Développement Web Java EE",
            "Design Patterns", "Sécurité des Applications", "Architecture Microservices",
            "Intelligence Artificielle & Java"
        };
        int idx = -1;
        for (int i = 0; i < titres.length; i++) {
            if (titres[i].equals(coursTitre)) { idx = i; break; }
        }
        if (idx < 0 || idx >= leconsByCours.length) return;

        for (String[] l : leconsByCours[idx]) {
            PreparedStatement ps = cnx.prepareStatement(
                "INSERT INTO `lecon` (`titre`, `description`, `idcours`) VALUES (?,?,?)");
            ps.setString(1, l[0]); ps.setString(2, l[1]); ps.setInt(3, coursId);
            ps.executeUpdate();
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
