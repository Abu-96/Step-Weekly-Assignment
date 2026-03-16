// File: Problem5.java
import java.util.*;
import java.util.concurrent.*;

public class Problem5 {

    // -----------------------------
    // Page View Event Class
    // -----------------------------
    static class PageViewEvent {
        String url;
        String userId;
        String source;

        PageViewEvent(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }

    // -----------------------------
    // Analytics Dashboard Class
    // -----------------------------
    private final HashMap<String, Integer> pageViews = new HashMap<>();
    private final HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    private final HashMap<String, Integer> trafficSources = new HashMap<>();
    private final int TOP_N = 10;

    // -----------------------------
    // Process each incoming event
    // -----------------------------
    public synchronized void processEvent(PageViewEvent event) {
        // Update total views per page
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // Update unique visitors
        uniqueVisitors.computeIfAbsent(event.url, k -> new HashSet<>()).add(event.userId);

        // Update traffic sources
        trafficSources.put(event.source, trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // -----------------------------
    // Display the dashboard
    // -----------------------------
    public synchronized void getDashboard() {
        // Top N pages using a min-heap
        PriorityQueue<Map.Entry<String, Integer>> topPagesHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            topPagesHeap.offer(entry);
            if (topPagesHeap.size() > TOP_N) {
                topPagesHeap.poll();
            }
        }

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();
        while (!topPagesHeap.isEmpty()) {
            topPages.add(topPagesHeap.poll());
        }
        Collections.reverse(topPages);

        // Print Top Pages
        System.out.println("\n--- Top Pages ---");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)");
            rank++;
        }

        // Print Traffic Sources
        int totalVisits = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\n--- Traffic Sources ---");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = totalVisits == 0 ? 0 : entry.getValue() * 100.0 / totalVisits;
            System.out.println(entry.getKey() + ": " + String.format("%.1f", percent) + "%");
        }
        System.out.println("---------------------------");
    }

    // -----------------------------
    // Start dashboard updates every 5 seconds
    // -----------------------------
    public void startDashboard() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::getDashboard, 5, 5, TimeUnit.SECONDS);
    }

    // -----------------------------
    // Main method to simulate incoming events
    // -----------------------------
    public static void main(String[] args) throws InterruptedException {
        Problem5 dashboard = new Problem5();

        // Start periodic dashboard updates
        dashboard.startDashboard();

        // Sample pages, sources, and users
        String[] urls = {"/article/breaking-news", "/sports/championship", "/tech/gadgets", "/health/fitness"};
        String[] sources = {"Google", "Facebook", "Direct", "Other"};
        Random rand = new Random();

        // Simulate incoming page view events
        while (true) {
            String url = urls[rand.nextInt(urls.length)];
            String user = "user_" + rand.nextInt(10000);
            String source = sources[rand.nextInt(sources.length)];

            dashboard.processEvent(new PageViewEvent(url, user, source));

            // Simulate high throughput (~100 events per second)
            Thread.sleep(10);
        }
    }
}