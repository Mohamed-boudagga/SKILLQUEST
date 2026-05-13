package tn.esprit.utils;

import tn.esprit.enties.User;

public class SessionManager {
    private static User currentUser;
    private static int currentUserId = 1;
    private static int currentUserLevel = 3;
    private static String addCourseReturnPath = "/gestionCours/GestionCours.fxml";

    private SessionManager() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user != null) {
            currentUserId = user.getId();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserLevel() {
        return currentUserLevel;
    }

    public static void setCurrentUserLevel(int level) {
        currentUserLevel = Math.max(1, Math.min(6, level));
    }

    public static String getAddCourseReturnPath() {
        return addCourseReturnPath;
    }

    public static void setAddCourseReturnPath(String returnPath) {
        addCourseReturnPath = returnPath;
    }

    public static void clear() {
        currentUser = null;
        currentUserId = 0;
        currentUserLevel = 1;
        addCourseReturnPath = "/gestionCours/GestionCours.fxml";
    }
}
