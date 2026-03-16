// File: Problem6.java
import java.util.*;
import java.util.concurrent.*;

public class Problem6 {

    // -----------------------------
    // Token Bucket class
    // -----------------------------
    static class TokenBucket {
        private final int maxTokens;
        private final double refillRatePerSecond; // tokens per second
        private double tokens;
        private long lastRefillTime;

        TokenBucket(int maxTokens, double refillRatePerHour) {
            this.maxTokens = maxTokens;
            this.refillRatePerSecond = refillRatePerHour / 3600.0;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Synchronized to be thread-safe
        public synchronized boolean allowRequest() {
            refillTokens();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            } else {
                return false;
            }
        }

        public synchronized int tokensRemaining() {
            refillTokens();
            return (int) tokens;
        }

        private void refillTokens() {
            long now = System.currentTimeMillis();
            double elapsedSeconds = (now - lastRefillTime) / 1000.0;
            tokens = Math.min(maxTokens, tokens + elapsedSeconds * refillRatePerSecond);
            lastRefillTime = now;
        }

        public synchronized long timeToNextTokenMillis() {
            if (tokens >= 1) return 0;
            return (long) (1000 / refillRatePerSecond);
        }
    }

    // -----------------------------
    // Rate Limiter Class
    // -----------------------------
    private final ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private final int maxRequestsPerHour = 1000;

    // -----------------------------
    // Check rate limit
    // -----------------------------
    public void checkRateLimit(String clientId) {
        TokenBucket bucket = clients.computeIfAbsent(clientId, k -> new TokenBucket(maxRequestsPerHour, maxRequestsPerHour));

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Client " + clientId + " -> Allowed (" + bucket.tokensRemaining() + " requests remaining)");
        } else {
            long retryAfter = bucket.timeToNextTokenMillis() / 1000;
            System.out.println("Client " + clientId + " -> Denied (0 requests remaining, retry after " + retryAfter + "s)");
        }
    }

    // -----------------------------
    // Get rate limit status
    // -----------------------------
    public void getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) {
            System.out.println("Client " + clientId + " -> No requests made yet.");
            return;
        }
        int used = maxRequestsPerHour - bucket.tokensRemaining();
        long resetInSeconds = bucket.timeToNextTokenMillis();
        System.out.println("Client " + clientId + " -> {used: " + used + ", limit: " + maxRequestsPerHour + ", reset in: " + resetInSeconds + "s}");
    }

    // -----------------------------
    // Main method to simulate requests
    // -----------------------------
    public static void main(String[] args) throws InterruptedException {
        Problem6 rateLimiter = new Problem6();

        String[] clients = {"abc123", "xyz789", "client001"};
        Random rand = new Random();

        // Simulate random requests
        for (int i = 0; i < 20; i++) {
            String client = clients[rand.nextInt(clients.length)];
            rateLimiter.checkRateLimit(client);

            if (i % 5 == 0) {
                rateLimiter.getRateLimitStatus(client);
            }

            Thread.sleep(50); // simulate 20 requests/sec
        }
    }
}