import java.util.*;
import java.util.concurrent.*;

class QuizQuestion {
    private String question;
    private List<String> options;
    private int correctOptionIndex;

    public QuizQuestion(String question, List<String> options, int correctOptionIndex) {
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}

class Quiz {
    private List<QuizQuestion> questions;
    private int score;
    private int correctAnswers;
    private int incorrectAnswers;
    private Scanner scanner;
    private ExecutorService executor;
    private boolean isAnswerSubmitted;

    public Quiz(List<QuizQuestion> questions) {
        this.questions = questions;
        this.score = 0;
        this.correctAnswers = 0;
        this.incorrectAnswers = 0;
        this.scanner = new Scanner(System.in);
        this.executor = Executors.newSingleThreadExecutor();
        this.isAnswerSubmitted = false;
    }

    public void startQuiz() {
        for (QuizQuestion question : questions) {
            displayQuestion(question);
            submitAnswerWithTimer(question);
            System.out.println(); 
        }
        displayResult();
        scanner.close();
        executor.shutdown();
    }

    private void displayQuestion(QuizQuestion question) {
        System.out.println(question.getQuestion());
        List<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.println("Enter an option: ");
    }

    private void submitAnswerWithTimer(QuizQuestion question) {
        isAnswerSubmitted = false;
        Future<Boolean> future = executor.submit(() -> {
            for (int i = 10; i > 0; i--) {
                System.out.print("\rTime left: " + i + " seconds   ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            if (!isAnswerSubmitted) {
                System.out.println("\nTime's up! No answer submitted for the current question.");
            }
            return false;
        });

        int chosenOptionIndex = -1;
        while (chosenOptionIndex < 0 || chosenOptionIndex >= question.getOptions().size()) {
            System.out.print("Enter your answer (1-" + question.getOptions().size() + "): ");
            try {
                chosenOptionIndex = Integer.parseInt(scanner.nextLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid option (1-" + question.getOptions().size() + ")");
            }
        }

        future.cancel(true);

        isAnswerSubmitted = true;
        if (question.getCorrectOptionIndex() == chosenOptionIndex) {
            System.out.println("Correct answer!");
            score++;
            correctAnswers++;
        } else {
            System.out.println("Incorrect answer.");
            incorrectAnswers++;
        }
    }

    private void displayResult() {
        System.out.println("Quiz ended!");
        System.out.println("Your score: " + score + "/" + questions.size());
        System.out.println("Correct answers: " + correctAnswers);
        System.out.println("Incorrect answers: " + incorrectAnswers);
    }
}

class Main {
    public static void main(String[] args) {
        List<QuizQuestion> questions = new ArrayList<>();
        questions.add(new QuizQuestion("What is the capital of France?", Arrays.asList("Paris", "London", "Berlin", "Rome"), 0));
        questions.add(new QuizQuestion("What is 2+2?", Arrays.asList("3", "4", "5", "6"), 1));
        questions.add(new QuizQuestion("Which planet is known as the Red Planet?", Arrays.asList("Venus", "Mars", "Jupiter", "Saturn"), 1));

        Quiz quiz = new Quiz(questions);
        quiz.startQuiz();
    }
}
