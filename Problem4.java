import java.util.*;

public class Problem4 {

    // n-gram index: ngram -> set of document IDs
    private HashMap<String, Set<String>> index = new HashMap<>();

    // store document ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    private int N = 5; // 5-gram

    // Extract n-grams from text
    private List<String> extractNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");

        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = extractNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());

            index.get(gram).add(docId);
        }
    }

    // Analyze plagiarism
    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = extractNgrams(text);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (index.containsKey(gram)) {

                for (String doc : index.get(gram)) {

                    matchCount.put(doc,
                            matchCount.getOrDefault(doc, 0) + 1);
                }
            }
        }

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            double similarity =
                    (matches * 100.0) / ngrams.size();

            System.out.println(
                    "Found " + matches +
                    " matching n-grams with \"" + doc + "\""
            );

            System.out.println(
                    "Similarity: " + similarity + "%"
            );

            if (similarity > 60) {
                System.out.println("PLAGIARISM DETECTED");
            }

            else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        Problem4 system = new Problem4();

        // Existing essays
        system.addDocument(
                "essay_089.txt",
                "machine learning algorithms are widely used in data science and artificial intelligence"
        );

        system.addDocument(
                "essay_092.txt",
                "machine learning algorithms are widely used in data science and artificial intelligence to build predictive models"
        );

        // New essay
        String newEssay =
                "machine learning algorithms are widely used in data science and artificial intelligence";

        System.out.println("Analyzing essay_123.txt\n");

        system.analyzeDocument("essay_123.txt", newEssay);
    }
}