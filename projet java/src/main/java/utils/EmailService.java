package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service pour l'envoi d'emails (Notifications).
 */
public class EmailService {

    // Configuration SMTP (Exemple pour Gmail)
    // NOTE : Pour Gmail, il faut utiliser un "Mot de passe d'application"
    private static final String SENDER_EMAIL = "mohamedboudagga6@gmail.com";
    private static final String SENDER_PASS  = "snxl rsmc gsdg orhv";

    public static void envoyerEmail(String destinataire, String sujet, String contenu) {
        // 1. Configuration des propriétés
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // 2. Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASS);
            }
        });

        // 3. Envoi asynchrone pour ne pas bloquer l'interface graphique
        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
                message.setSubject(sujet);
                message.setText(contenu);

                Transport.send(message);
                System.out.println("📧 Email envoyé avec succès à : " + destinataire);
            } catch (MessagingException e) {
                System.err.println("✘ Erreur lors de l'envoi de l'email : " + e.getMessage());
                // Note : L'erreur est souvent due à des identifiants non configurés
            }
        }).start();
    }
}
