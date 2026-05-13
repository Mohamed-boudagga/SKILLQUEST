-- ============================================================
-- SkillQuest — Script de MIGRATION v1 → v2
-- A utiliser si la base 'skillquest' existe déjà
-- et que vous voulez juste AJOUTER les nouvelles colonnes
-- sans tout effacer.
-- ============================================================
USE `skillquest`;

-- Ajouter telephone à etudiant (si elle n'existe pas)
ALTER TABLE `etudiant`
    ADD COLUMN IF NOT EXISTS `telephone`  VARCHAR(20)   DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS `sexe`       ENUM('M','F') DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS `est_bloque` TINYINT(1)    NOT NULL DEFAULT 0;

-- Créer la table admin (si elle n'existe pas)
CREATE TABLE IF NOT EXISTS `admin` (
    `id`           INT          NOT NULL AUTO_INCREMENT,
    `nom`          VARCHAR(100) NOT NULL,
    `prenom`       VARCHAR(100) NOT NULL,
    `email`        VARCHAR(150) NOT NULL UNIQUE,
    `mot_de_passe` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Créer la table cours (si elle n'existe pas)
CREATE TABLE IF NOT EXISTS `cours` (
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

-- Insérer un admin par défaut (si la table est vide)
INSERT IGNORE INTO `admin` (`nom`, `prenom`, `email`, `mot_de_passe`)
VALUES ('Admin', 'Principal', 'admin@skillquest.tn', 'admin123');

-- Vérification
DESCRIBE `etudiant`;
DESCRIBE `admin`;
DESCRIBE `cours`;

-- ============================================================
-- Migration terminée !
-- ============================================================
