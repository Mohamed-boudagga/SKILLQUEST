package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire pour le hachage sécurisé des mots de passe avec BCrypt.
 */
public class PasswordUtils {

    /**
     * Hache un mot de passe en utilisant BCrypt.
     * @param plainPassword Le mot de passe en clair.
     * @return Le haché (hash) du mot de passe.
     */
    public static String hashPassword(String plainPassword) {
        // Un sel (salt) est généré automatiquement par BCrypt.gensalt()
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un haché.
     * @param plainPassword Le mot de passe en clair.
     * @param hashedPassword Le haché stocké en base de données.
     * @return true si ça correspond, false sinon.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            // En cas d'erreur (ex: haché malformé), on retourne false
            return false;
        }
    }
}
