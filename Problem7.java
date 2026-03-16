// File: Problem7.java
import java.util.*;

public class Problem7 {

    // -----------------------------
    // Trie Node Class
    // -----------------------------
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        int frequency = 0; // frequency of the query
    }

    // -----------------------------
    // Autocomplete System
    // -----------------------------
    private final TrieNode root = new TrieNode();

    // -----------------------------
    // Insert a search query with frequency update
    // -----------------------------
    public synchronized void insert(String query) {
        TrieNode node = root;
        for (char ch : query.toCharArray()) {
            node = node.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        node.isEndOfWord = true;
        node.frequency += 1;
    }

    // -----------------------------
    // Update frequency of a query
    // -----------------------------
    public synchronized void updateFrequency(String query) {
        insert(query); // simply call insert to increment frequency
        System.out.println("Updated frequency for query: \"" + query + "\"");
    }

    // -----------------------------
    // Get top K suggestions for a prefix
    // -----------------------------
    public synchronized List<String> getSuggestions(String prefix, int K) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) {
                return Collections.emptyList(); // no suggestions
            }
        }

        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        dfs(node, new StringBuilder(prefix), minHeap, K);

        List<String> results = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            results.add(0, minHeap.poll().getKey()); // reverse order: highest freq first
        }
        return results;
    }

    // -----------------------------
    // DFS to traverse Trie and collect top K queries
    // -----------------------------
    private void dfs(TrieNode node, StringBuilder sb, PriorityQueue<Map.Entry<String, Integer>> heap, int K) {
        if (node.isEndOfWord) {
            heap.offer(new AbstractMap.SimpleEntry<>(sb.toString(), node.frequency));
            if (heap.size() > K) {
                heap.poll();
            }
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            sb.append(entry.getKey());
            dfs(entry.getValue(), sb, heap, K);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    // -----------------------------
    // Main method to simulate autocomplete
    // -----------------------------
    public static void main(String[] args) {
        Problem7 autocomplete = new Problem7();

        // Simulate inserting previous search queries
        String[] queries = {
                "java tutorial", "javascript", "java download", "java 21 features",
                "python tutorial", "python download", "c++ tutorial", "c++ reference",
                "java interview questions", "javascript tutorial"
        };

        for (String q : queries) {
            int freq = new Random().nextInt(1000) + 1; // random frequency
            for (int i = 0; i < freq; i++) {
                autocomplete.insert(q);
            }
        }

        // Simulate user typing
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter prefix to autocomplete:");
        String prefix = sc.nextLine();

        List<String> suggestions = autocomplete.getSuggestions(prefix, 10);
        System.out.println("\nTop suggestions for prefix \"" + prefix + "\":");
        int rank = 1;
        for (String s : suggestions) {
            System.out.println(rank + ". " + s);
            rank++;
        }

        // Simulate updating frequency
        autocomplete.updateFrequency(prefix + " new");
        sc.close();
    }
}