import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single student's complete grade record.
 * 
 * Each StudentRecord contains:
 * - The student's ID and name
 * - A map of total scores by category (e.g., exams, homework)
 * - A nested map of detailed assignment scores per category
 *
 * This class supports adding new category totals and
 * individual assignment scores, and computing total points.
 */
public class StudentRecord {

    /** Unique student identifier. */
    private String id;

    /** Full name of the student. */
    private String name;

    /** Stores total scores per grading category. */
    private Map<String, Double> categoryScores;

    /** Stores detailed assignment scores grouped by category. */
    private Map<String, Map<String, Double>> detailedScores;

    /**
     * Constructs a new StudentRecord with the given ID and name.
     *
     * @param id   the student ID
     * @param name the student's full name
     */
    public StudentRecord(String id, String name) {
        this.id = id;
        this.name = name;
        this.categoryScores = new HashMap<>();
        this.detailedScores = new HashMap<>();
    }

    /**
     * Returns the student's ID.
     *
     * @return the student ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the student's full name.
     *
     * @return the student name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the map containing category totals.
     *
     * @return map of categories and total scores
     */
    public Map<String, Double> getCategoryScores() {
        return categoryScores;
    }

    /**
     * Returns the nested map containing detailed assignment scores.
     *
     * @return map of categories and their assignment score maps
     */
    public Map<String, Map<String, Double>> getDetailedScores() {
        return detailedScores;
    }

    /**
     * Adds or updates a total score for the specified category.
     * If the category already exists, the score is added to its current value.
     *
     * @param category the grading category (e.g., "homework")
     * @param score    the score to add to this category
     */
    public void addCategoryScore(String category, double score) {
        categoryScores.put(category, categoryScores.getOrDefault(category, 0.0) + score);
    }

    /**
     * Adds an individual assignment score within a category.
     *
     * @param category   the category name (e.g., "quizzes")
     * @param assignment the assignment name (e.g., "Quiz1")
     * @param score      the score earned on that assignment
     */
    public void addAssignment(String category, String assignment, double score) {
        detailedScores.putIfAbsent(category, new HashMap<>());
        detailedScores.get(category).put(assignment, score);
    }

    /**
     * Calculates and returns the total of all category scores.
     *
     * @return the total accumulated score across all categories
     */
    public double getTotalScore() {
        return categoryScores.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
