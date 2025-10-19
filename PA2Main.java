/**
 * Entry point for Programming Assignment - Streams and Hash Table.
 *
 * This class drives the gradebook merging process. It reads multiple
 * CSV gradebook files (each representing a category such as exams,
 * homework, or quizzes) and produces two output CSV files:
 *
 * 1. master_details.csv - Full per-student assignment breakdown.
 * 2. summary.csv - Normalized category totals and overall percentages.
 *
 * The program uses GradebookMerger to parse, process, and generate
 * the required output files. It supports quoted CSV fields and
 * handles multiple files through command-line arguments.
 *
 * Example usage:
 * java PA2Main homework_1.csv homework_2.csv quizzes_1.csv quizzes_2.csv
 * exams_1.csv exams_2.csv
 */
public class PA2Main {

    /**
     * The main method that starts the gradebook merging process.
     *
     * @param args list of CSV file names to merge (each one a separate grade
     *             category)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java PA2Main <csv1> <csv2> ...");
            return;
        }

        GradebookMerger merger = new GradebookMerger();
        try {
            // Read and process all CSV files provided in arguments
            for (String file : args) {
                merger.readCSV(file);
            }

            // Generate output files
            merger.generateDetailsFile("master_details.csv");
            merger.generateSummaryFile("summary.csv");

            System.out.println("✅ master_details.csv and summary.csv generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
