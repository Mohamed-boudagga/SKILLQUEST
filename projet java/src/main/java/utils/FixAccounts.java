package utils;

import models.Etudiant;
import services.ServiceEtudiant;

public class FixAccounts {
    public static void main(String[] args) {
        ServiceEtudiant service = new ServiceEtudiant();
        
        String[] emails = {
            "Mohamed.Boudagga@esprit.tn",
            "oussema.briki2003@gmail.com"
        };
        
        for (String email : emails) {
            Etudiant et = service.getByEmail(email);
            if (et != null) {
                // On met "123456" et le service va le hacher automatiquement 
                // car nous avons mis à jour ServiceEtudiant.update précédemment !
                et.setMotDePasse("123456");
                service.update(et);
                System.out.println("✅ Compte réparé : " + email);
            } else {
                System.out.println("❌ Email non trouvé : " + email);
            }
        }
        System.exit(0);
    }
}
