package main;

import entities.*;
import services.*;
import java.sql.Date;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        ServiceGames sg = new ServiceGames();
        ServiceBattle sb = new ServiceBattle();
        ServiceJoueur sj = new ServiceJoueur();
        ServicePartie sp = new ServicePartie();

        // Ajout d'un Game
        Games game = new Games(0, "Quiz", "Facile", 60, 100);
        sg.ajouter(game);
        System.out.println("Game ajouté avec succès !");

        // Ajout d'une Battle avec les bons types (LocalDateTime)
        Battle battle = new Battle(0, "duel", "ongoing", LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), "Joueur1");
        sb.ajouter(battle);
        System.out.println("Battle ajoutée avec succès !");

        // Ajout d'un Joueur
        Joueur joueur = new Joueur(0, 1, 20, 1500, 1, "winner");
        sj.ajouter(joueur);
        System.out.println("Joueur ajouté avec succès !");

        // Ajout d'une Partie
        Joueur j = new Joueur(1, 1, 30, 100, 4, "active");
        Partie partie = new Partie(0, 90, Date.valueOf("2024-05-01"), j);
        sp.ajouter(partie);
        System.out.println("Partie ajoutée avec succès !");

        System.out.println("\n--- TOUTES LES DONNÉES ONT ÉTÉ INSÉRÉES DANS LA WARZONE ---");
    }
}