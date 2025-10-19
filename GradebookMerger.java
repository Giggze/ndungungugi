import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Combines multiple section CSV gradebooks into master and summary gradebooks.
 * Supports quoted CSV fields, per-category normalization, and output
 * generation.
 *
 * Produces:
 * - master_details.csv (full per-assignment breakdown)
 * - summary.csv (per-student category and normalized overall percentage)
 *
 * Uses: HashMap, ArrayList, and Java Streams per assignment requirements.
 */
public class GradebookMerger {
    private Map<String, StudentRecord> students = new HashMap<>();
    private Map<String, Double> categoryMaxTotal = new HashMap<>();

    /**
     * Reads one section CSV and adds its grades into the master structure.
     */
    public void readCSV(String fileName) throws IOException {
        String category = fileName.substring(0, fileName.indexOf("_"));
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        if (lines.size() < 3)
            return;

        String[] headers = parseCSVLine(lines.get(0));
        String[] maxLine = parseCSVLine(lines.get(1));

        // Compute maximum total points for this category
        double categoryMax = 0.0;
        for (int i = 3; i < maxLine.length; i++) {
            try {
                categoryMax += Double.parseDouble(maxLine[i].replace("\"", "").trim());
            } catch (NumberFormatException ignore) {
            }
        }
        categoryMaxTotal.put(category, categoryMaxTotal.getOrDefault(category, 0.0) + categoryMax);

        // Process student rows
        for (int i = 2; i < lines.size(); i++) {
            String[] parts = parseCSVLine(lines.get(i));
            if (parts.length < 3)
                continue;

            String id = parts[0].replace("\"", "").trim();
            String name = parts[1].replace("\"", "").trim();

            double categoryTotal = 0.0;
            try {
                categoryTotal = Double.parseDouble(parts[2].replace("\"", "").trim());
            } catch (NumberFormatException e) {
                continue; // skip malformed rows
            }

            students.putIfAbsent(id, new StudentRecord(id, name));
            students.get(id).addCategoryScore(category, categoryTotal);

            // Individual assignments
            for (int j = 3; j < parts.length && j < headers.length; j++) {
                String assignment = headers[j].replace("\"", "").trim();
                String value = parts[j].replace("\"", "").trim();
                if (!value.isEmpty()) {
                    try {
                        double val = Double.parseDouble(value);
                        students.get(id).addAssignment(category, assignment, val);
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        }
    }

    /**
     * Writes detailed per-assignment file.
     */
    public void generateDetailsFile(String outFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
            writer.println("ID,Name,Category,Assignment,Score");
            for (StudentRecord s : students.values()) {
                for (String category : s.getDetailedScores().keySet()) {
                    for (var entry : s.getDetailedScores().get(category).entrySet()) {
                        writer.printf("%s,%s,%s,%s,%.2f%n",
                                s.getId(), s.getName(), category, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * Writes summary file with normalized overall percentages.
     */
    public void generateSummaryFile(String outFile) throws IOException {
        List<String> categories = students.values().stream()
                .flatMap(s -> s.getCategoryScores().keySet().stream())
                .distinct().collect(Collectors.toList());

        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
            writer.print("ID,Name,Overall(%))");
            for (String c : categories)
                writer.print("," + c);
            writer.println();

            for (StudentRecord s : students.values()) {
                double weightedSum = 0.0;
                double totalWeight = 0.0;

                for (String c : categories) {
                    double score = s.getCategoryScores().getOrDefault(c, 0.0);
                    double max = categoryMaxTotal.getOrDefault(c, 1.0);
                    if (max > 0) {
                        weightedSum += (score / max);
                        totalWeight += 1.0;
                    }
                }

                double overallPercent = (weightedSum / totalWeight) * 100.0;

                writer.printf("%s,%s,%.2f", s.getId(), s.getName(), overallPercent);
                for (String c : categories) {
                    writer.printf(",%.2f", s.getCategoryScores().getOrDefault(c, 0.0));
                }
                writer.println();
            }
        }
    }

    /**
     * Helper parser to correctly handle commas inside quotes.
     */
    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}
