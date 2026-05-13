import models.Certification;
import models.Exam;
import services.ServiceCertification;
import services.ServiceExam;

import java.util.Date;

public class Main {

    public static void main(String[] args) {

        // --- NETTOYAGE ET INSERTION DE DONNÉES DE TEST POUR LE NIVEAU 1 ---
        ServiceExam se = new ServiceExam();
        ServiceCertification sc = new ServiceCertification();

        // 1. Création d'un Examen pour le Niveau 1
        Exam eTest = new Exam();
        eTest.setNom("Java Mastery Exam");
        eTest.setLevel(1);
        eTest.setDureeMinutes(45);
        se.add(eTest);

        // 2. Création de la Certification pour le Niveau 1
        Certification cTest = new Certification();
        cTest.setTitle("Oracle Certified Associate");
        cTest.setLevel(1);
        cTest.setDescription("Expert en programmation Java Orientée Objet et bases du langage. Félicitations pour ce succès remarquable !");
        cTest.setDateObtention(new Date());
        sc.add(cTest);

        System.out.println("Données de test ajoutées avec succès pour le Niveau 1 !");
        System.out.println("Vérification Exams : " + se.getAll());
        System.out.println("Vérification Certifs : " + sc.getAll());
    }
}