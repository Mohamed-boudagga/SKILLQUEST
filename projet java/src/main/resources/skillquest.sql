-- ============================================================
-- SkillQuest — Script SQL complet v2
-- Modules : Etudiant (avec telephone, sexe, blocage) + Admin + Cours
-- Base de donnees : skillquest
-- ============================================================
-- ETAPES :
--   1. Ouvrir phpMyAdmin (http://localhost/phpmyadmin)
--   2. Cliquer sur "SQL" en haut
--   3. Copier-coller tout ce fichier et cliquer "Executer"
-- ============================================================

-- 1) Créer la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS `skillquest`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `skillquest`;

-- ============================================================
-- 2) TABLE : admin
-- ============================================================
DROP TABLE IF EXISTS `cours`;
DROP TABLE IF EXISTS `etudiant`;
DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
    `id`           INT          NOT NULL AUTO_INCREMENT,
    `nom`          VARCHAR(100) NOT NULL,
    `prenom`       VARCHAR(100) NOT NULL,
    `email`        VARCHAR(150) NOT NULL UNIQUE,
    `mot_de_passe` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3) TABLE : etudiant  (avec telephone, sexe, est_bloque)
-- ============================================================
CREATE TABLE `etudiant` (
    `id`           INT           NOT NULL AUTO_INCREMENT,
    `nom`          VARCHAR(100)  NOT NULL,
    `prenom`       VARCHAR(100)  NOT NULL,
    `email`        VARCHAR(150)  NOT NULL UNIQUE,
    `mot_de_passe` VARCHAR(255)  NOT NULL,
    `niveau`       INT           NOT NULL DEFAULT 1,
    `points`       INT           NOT NULL DEFAULT 0,
    `est_mentor`   TINYINT(1)    NOT NULL DEFAULT 0,
    `telephone`    VARCHAR(20)   DEFAULT NULL,           -- NOUVEAU
    `sexe`         ENUM('M','F') DEFAULT NULL,           -- NOUVEAU
    `est_bloque`   TINYINT(1)    NOT NULL DEFAULT 0,     -- NOUVEAU
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4) TABLE : cours  (ajoutée par l'admin)
-- ============================================================
CREATE TABLE `cours` (
    `id`            INT          NOT NULL AUTO_INCREMENT,
    `titre`         VARCHAR(200) NOT NULL,
    `description`   TEXT,
    `niveau_requis` INT          NOT NULL DEFAULT 1,
    `admin_id`      INT          NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_cours_admin`
        FOREIGN KEY (`admin_id`) REFERENCES `admin`(`id`)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5) Données de test — Admins
-- ============================================================
INSERT INTO `admin` (`nom`, `prenom`, `email`, `mot_de_passe`) VALUES
('Admin',    'Principal', 'admin@skillquest.tn', 'admin123'),
('Directeur','Sami',      'sami@skillquest.tn',  'sami2024');

-- ============================================================
-- 6) Données de test — Étudiants (avec telephone & sexe)
-- ============================================================
INSERT INTO `etudiant`
    (`nom`, `prenom`, `email`, `mot_de_passe`, `niveau`, `points`, `est_mentor`, `telephone`, `sexe`, `est_bloque`)
VALUES
('Ben Ali',  'Ahmed',  'ahmed@esprit.tn',  'pass123',      1,   0,   0, '20123456', 'M', 0),
('Trabelsi', 'Sarra',  'sarra@esprit.tn',  'pass456',      2, 150,   0, '22334455', 'F', 0),
('Mansouri', 'Khalil', 'khalil@esprit.tn', 'pass789',      3, 500,   1, '55667788', 'M', 0),
('Briki',    'Oussama','oussama@esprit.tn','pass1234',     1,   0,   0, '98765432', 'M', 0),
('Boudagga', 'Mohamed','mohamed@esprit.tn','pass12345678', 5,1000,   1, '27182818', 'M', 0);

-- ============================================================
-- 7) Données de test — Cours (créés par admin id=1)
-- ============================================================
INSERT INTO `cours` (`titre`, `description`, `niveau_requis`, `admin_id`) VALUES
('Java Débutant',      'Introduction à la programmation Java.',          1, 1),
('Java Avancé',        'Génériques, Collections, Streams et Lambdas.',   3, 1),
('Base de Données SQL','Conception et requêtes SQL avec MySQL.',          2, 1),
('Développement Web',  'HTML, CSS, JavaScript et frameworks modernes.',  2, 2),
('Design Patterns',    'Patrons de conception GoF en Java.',             4, 2);

-- ============================================================
-- 8) Vérification
-- ============================================================
SELECT 'ADMINS' AS table_name;
SELECT * FROM `admin`;

SELECT 'ETUDIANTS' AS table_name;
SELECT * FROM `etudiant`;

SELECT 'COURS' AS table_name;
SELECT * FROM `cours`;

-- ============================================================
-- Fin du script — Base de données SkillQuest v2 prête !
-- ============================================================
