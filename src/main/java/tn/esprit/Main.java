package tn.esprit;

import tn.esprit.enties.Cours;
import tn.esprit.enties.Lecon;
import tn.esprit.enties.Role;
import tn.esprit.enties.User;
import tn.esprit.services.CoursService;
import tn.esprit.services.LeconService;
import tn.esprit.services.UserService;

import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args){
        CoursService coursService = new CoursService();
        LeconService leconService = new LeconService();
        UserService userService = new UserService();
        try {
            // ---- TEST COURS ----
            System.out.println("=== TEST COURS ===");
            coursService.add(new Cours("JAVA","JAVA est un langage de programation orionter objet","3", "", 1));
            coursService.add(new Cours("Python", "Langage de programmation", "2", "", 1));
            System.out.println("getAll: " + coursService.getAll());
            System.out.println("getById: " + coursService.getById(1));
            System.out.println("getByTitre: " + coursService.getByTitre("JAVA"));
            System.out.println("getByNiveau: " + coursService.getByNiveau("3"));

            // ---- TEST LECON ----
            System.out.println("=== TEST LECON ===");
            Cours c = coursService.getById(1);
            leconService.add(new Lecon("Lecon 1", "Introduction a JAVA", c));
            System.out.println("getAll: " + leconService.getAll());
            System.out.println("getById: " + leconService.getById(1));
            System.out.println("getByTitre: " + leconService.getByTitre("Lecon 1"));

            // ---- TEST USER ----
            System.out.println("=== TEST USER ===");
            userService.add(new User("Ahmed", "ahmed@esprit.tn", Role.Student));
            userService.add(new User("Wiss", "wiss@esprit.tn", Role.Admin));
            System.out.println("getAll: " + userService.getAll());
            System.out.println("getById: " + userService.getById(1));
            System.out.println("getByName: " + userService.getByName("Ahmed"));
            System.out.println("getByRole: " + userService.getByRole(Role.Student));





        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    /*   UserService userService =new UserService();
       try {
           userService.add(new User("ons","wiss.wiss@esprit.tn", Role.Admin));
           userService.update(new User(1,"wiss1","wiss.wiss@esprit.tn", Role.Student));
System.out.println(userService.getAll());
userService.delete(1);
       } catch (SQLException e) {
System.out.println(e.getMessage());       }*/


    }
}
