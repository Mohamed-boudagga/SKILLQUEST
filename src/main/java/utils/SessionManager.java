package utils;

import models.Exam;
import services.ServiceExam;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionManager {
    private static SessionManager instance;
    
    private int currentStudentLevel = 0; // On commence au niveau 0
    private Set<Integer> completedExamIds = new HashSet<>();
    private boolean isAdmin = false;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getCurrentLevel() {
        return currentStudentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentStudentLevel = level;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Marque un examen comme réussi et vérifie si le niveau suivant doit être débloqué.
     */
    public boolean completeExam(Exam exam) {
        completedExamIds.add(exam.getId());
        
        // Vérifier si tous les examens de ce niveau sont réussis
        ServiceExam se = new ServiceExam();
        List<Exam> allExamsInLevel = se.getAll().stream()
                .filter(e -> e.getLevel() == exam.getLevel())
                .collect(Collectors.toList());
        
        List<Integer> allIdsInLevel = allExamsInLevel.stream()
                .map(Exam::getId)
                .collect(Collectors.toList());
        
        if (completedExamIds.containsAll(allIdsInLevel)) {
            // Tous les examens du niveau sont finis !
            if (exam.getLevel() == currentStudentLevel) {
                currentStudentLevel++;
                return true; // Niveau débloqué !
            }
        }
        return false;
    }

    public boolean isExamCompleted(int examId) {
        return completedExamIds.contains(examId);
    }
}
