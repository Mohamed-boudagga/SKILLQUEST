USE skillquestproject;

ALTER TABLE cours
    ADD COLUMN IF NOT EXISTS contenue TEXT NULL;

ALTER TABLE cours
    ADD COLUMN IF NOT EXISTS idAjouteur INT NULL;

ALTER TABLE cours
    ADD COLUMN IF NOT EXISTS dateDeCreation DATETIME NULL;

UPDATE cours
SET contenue = ''
WHERE contenue IS NULL;

UPDATE cours
SET idAjouteur = 1
WHERE idAjouteur IS NULL OR idAjouteur <= 0;

UPDATE cours
SET dateDeCreation = NOW()
WHERE dateDeCreation IS NULL;

UPDATE cours
SET niveau = '1'
WHERE niveau IS NULL OR niveau NOT REGEXP '^[1-6]$';

ALTER TABLE cours
    MODIFY COLUMN niveau INT NOT NULL;

ALTER TABLE cours
    MODIFY COLUMN idAjouteur INT NOT NULL;

ALTER TABLE cours
    MODIFY COLUMN dateDeCreation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
