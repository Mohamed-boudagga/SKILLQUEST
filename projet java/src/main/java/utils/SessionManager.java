package utils;

import models.gestionTestCertif.Exam;
import services.gestionTestCertif.ServiceExam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionManager {
    private static SessionManager instance;
    private int currentStudentLevel = 0;
    private Set<Integer> completedExamIds = new HashSet<>();
    private boolean isAdmin = false;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public int getCurrentLevel() { return currentStudentLevel; }
    public void setCurrentLevel(int level) { this.currentStudentLevel = level; }
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public boolean completeExam(Exam exam) {
        completedExamIds.add(exam.getId());
        ServiceExam se = new ServiceExam();
        List<Integer> allIdsInLevel = se.getAll().stream()
                .filter(e -> e.getLevel() == exam.getLevel())
                .map(Exam::getId)
                .collect(Collectors.toList());
        if (completedExamIds.containsAll(allIdsInLevel)) {
            if (exam.getLevel() == currentStudentLevel) { currentStudentLevel++; return true; }
        }
        return false;
    }

    public boolean isExamCompleted(int examId) { return completedExamIds.contains(examId); }
}
