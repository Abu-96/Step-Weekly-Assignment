// File: Problem10.java
import java.util.*;

public class Problem10 {

    // -----------------------------
    // Video Data Class
    // -----------------------------
    static class Video {
        String id;
        String content; // could be file path or metadata
        int accessCount;

        Video(String id, String content) {
            this.id = id;
            this.content = content;
            this.accessCount = 0;
        }
    }

    // -----------------------------
    // LRU Cache using LinkedHashMap
    // -----------------------------
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true); // access-order
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    // -----------------------------
    // Multi-Level Cache System
    // -----------------------------
    private final LRUCache<String, Video> L1;
    private final Map<String, Video> L2;
    private final Map<String, Video> L3; // Database

    private int L1Hits = 0, L2Hits = 0, L3Hits = 0;
    private int L1Requests = 0, L2Requests = 0, L3Requests = 0;

    public Problem10(int L1Capacity, int L2Capacity, int totalVideos) {
        L1 = new LRUCache<>(L1Capacity);
        L2 = new HashMap<>();
        L3 = new HashMap<>();

        // Populate L3 database with totalVideos
        for (int i = 1; i <= totalVideos; i++) {
            String vid = "video_" + i;
            Video video = new Video(vid, "Content for " + vid);
            L3.put(vid, video);
            if (L2.size() < L2Capacity) {
                L2.put(vid, video); // L2 contains some frequently accessed
            }
        }
    }

    // -----------------------------
    // Get video with multi-level lookup
    // -----------------------------
    public Video getVideo(String videoId) {
        L1Requests++;
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println(videoId + " → L1 Cache HIT (0.5ms)");
            return L1.get(videoId);
        }

        L2Requests++;
        if (L2.containsKey(videoId)) {
            L2Hits++;
            System.out.println(videoId + " → L1 Cache MISS → L2 Cache HIT (5ms)");
            Video v = L2.get(videoId);
            promoteToL1(v);
            return v;
        }

        L3Requests++;
        if (L3.containsKey(videoId)) {
            L3Hits++;
            System.out.println(videoId + " → L1 Cache MISS → L2 Cache MISS → L3 DB HIT (150ms)");
            Video v = L3.get(videoId);
            addToL2(v);
            return v;
        }

        System.out.println(videoId + " → Not found in any cache!");
        return null;
    }

    private void promoteToL1(Video video) {
        video.accessCount++;
        L1.put(video.id, video);
    }

    private void addToL2(Video video) {
        video.accessCount = 1;
        L2.put(video.id, video);
    }

    // -----------------------------
    // Print Cache Statistics
    // -----------------------------
    public void getStatistics() {
        double L1HitRate = L1Requests == 0 ? 0 : (L1Hits * 100.0 / L1Requests);
        double L2HitRate = L2Requests == 0 ? 0 : (L2Hits * 100.0 / L2Requests);
        double L3HitRate = L3Requests == 0 ? 0 : (L3Hits * 100.0 / L3Requests);

        System.out.printf("\nCache Statistics:\n");
        System.out.printf("L1: Hit Rate %.1f%%, Avg Time: 0.5ms\n", L1HitRate);
        System.out.printf("L2: Hit Rate %.1f%%, Avg Time: 5ms\n", L2HitRate);
        System.out.printf("L3: Hit Rate %.1f%%, Avg Time: 150ms\n", L3HitRate);

        double overallHitRate = (L1Hits + L2Hits + L3Hits) * 100.0 / (L1Requests + L2Requests + L3Requests);
        System.out.printf("Overall: Hit Rate %.1f%%\n", overallHitRate);
    }

    // -----------------------------
    // Main Method with Sample I/O
    // -----------------------------
    public static void main(String[] args) {
        Problem10 cacheSystem = new Problem10(3, 5, 10); // L1=3, L2=5, L3=10 videos

        // Access some videos
        cacheSystem.getVideo("video_1"); // miss L1, L2 HIT, promoted
        cacheSystem.getVideo("video_1"); // L1 HIT
        cacheSystem.getVideo("video_6"); // miss L1, L2 MISS, L3 HIT → added to L2
        cacheSystem.getVideo("video_3"); // L1 MISS, L2 HIT → promoted
        cacheSystem.getVideo("video_7"); // L3 HIT

        cacheSystem.getStatistics();
    }
}