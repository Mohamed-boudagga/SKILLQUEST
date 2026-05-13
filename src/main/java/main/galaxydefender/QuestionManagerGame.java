package main.galaxydefender;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Manages the bank of quiz questions and provides random selection.
 */
public class QuestionManagerGame {
    public static class Question {
        public final String text;
        public final String answer;

        public Question(String text, String answer) {
            this.text = text;
            this.answer = answer;
        }

        public boolean checkAnswer(String userAnswer) {
            return answer.equalsIgnoreCase(userAnswer.trim());
        }
    }

    private static final List<Question> QUESTIONS = Arrays.asList(
            new Question("Quelle est la classe mère de toutes les classes ?", "Object"),
            new Question("Quel est le mot-clé pour héritage?", "extends"),
            new Question("Quel est le mot-clé pour implementer une interface?", "implements"),
            new Question("Quelle Interface pour tri naturel?", "Comparable"),
            new Question("Quelle Interface pour tri personnalisé?", "Comparator"),
            new Question("Quel est le Package des collections ?", "java.util"),
            new Question("Quelle est la méthode pour la comparaison contenu objet ?", "equals()"),
            new Question("Quelle est la méthode responsable sur la conversion objet > texte?", "toString()"),
            new Question("Quelle est la méthode de hachage?", "hashcode()"),
            new Question("Java supporte héritage multiple ?", "non")
    );

    private final Random random = new Random();

    public Question getRandomQuestion() {
        return QUESTIONS.get(random.nextInt(QUESTIONS.size()));
    }
}
