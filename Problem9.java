// File: Problem9.java
import java.util.*;

public class Problem9 {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long timestamp; // in milliseconds

        Transaction(int id, int amount, String merchant, String account, long timestamp) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "id:" + id + " amt:" + amount + " merchant:" + merchant + " acc:" + account;
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    // Classic Two-Sum
    public List<int[]> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction tx : transactions) {
            int complement = target - tx.amount;
            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, tx.id});
            }
            map.put(tx.amount, tx);
        }
        return result;
    }

    // Two-Sum within 1-hour window
    public List<int[]> findTwoSumTimeWindow(int target, long windowMillis) {
        Map<Integer, List<Transaction>> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction tx : transactions) {
            int complement = target - tx.amount;
            if (map.containsKey(complement)) {
                for (Transaction t : map.get(complement)) {
                    if (Math.abs(t.timestamp - tx.timestamp) <= windowMillis) {
                        result.add(new int[]{t.id, tx.id});
                    }
                }
            }
            map.computeIfAbsent(tx.amount, k -> new ArrayList<>()).add(tx);
        }
        return result;
    }

    // K-Sum using recursion (simplified for small K)
    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> results = new ArrayList<>();
        transactions.sort(Comparator.comparingInt(t -> t.amount));
        kSumHelper(0, k, target, new ArrayList<>(), results);
        return results;
    }

    private void kSumHelper(int start, int k, int target, List<Integer> path, List<List<Integer>> results) {
        if (k == 2) {
            int left = start, right = transactions.size() - 1;
            while (left < right) {
                int sum = transactions.get(left).amount + transactions.get(right).amount;
                if (sum == target) {
                    List<Integer> pair = new ArrayList<>(path);
                    pair.add(transactions.get(left).id);
                    pair.add(transactions.get(right).id);
                    results.add(pair);
                    left++;
                    right--;
                } else if (sum < target) left++;
                else right--;
            }
            return;
        }

        for (int i = start; i < transactions.size(); i++) {
            path.add(transactions.get(i).id);
            kSumHelper(i + 1, k - 1, target - transactions.get(i).amount, path, results);
            path.remove(path.size() - 1);
        }
    }

    // Duplicate detection: same amount, same merchant, different accounts
    public List<String> detectDuplicates() {
        Map<String, Set<String>> map = new HashMap<>();
        List<String> duplicates = new ArrayList<>();

        for (Transaction tx : transactions) {
            String key = tx.amount + "_" + tx.merchant;
            map.computeIfAbsent(key, k -> new HashSet<>());
            if (!map.get(key).add(tx.account)) {
                duplicates.add("Duplicate found: amount=" + tx.amount + ", merchant=" + tx.merchant);
            }
        }
        return duplicates;
    }

    // -----------------------------
    // Main method with sample I/O
    // -----------------------------
    public static void main(String[] args) {
        Problem9 system = new Problem9();

        long now = System.currentTimeMillis();
        system.addTransaction(new Transaction(1, 500, "Store A", "acc1", now));
        system.addTransaction(new Transaction(2, 300, "Store B", "acc2", now + 15*60*1000));
        system.addTransaction(new Transaction(3, 200, "Store C", "acc3", now + 30*60*1000));
        system.addTransaction(new Transaction(4, 500, "Store A", "acc2", now + 45*60*1000));

        System.out.println("Two-Sum target=500 → " + system.findTwoSum(500));
        System.out.println("Two-Sum 1-hour window target=500 → " + system.findTwoSumTimeWindow(500, 3600*1000));
        System.out.println("K-Sum k=3, target=1000 → " + system.findKSum(3, 1000));
        System.out.println("Duplicate detection → " + system.detectDuplicates());
    }
}