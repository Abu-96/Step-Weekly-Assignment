import java.util.*;

public class Problem3 {

    // DNS Entry class
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, int ttl) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttl * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // Cache storage
    private LinkedHashMap<String, DNSEntry> cache;

    private int capacity;
    private int hits = 0;
    private int misses = 0;

    public Problem3(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > Problem3.this.capacity;
            }
        };
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                System.out.println("Cache HIT -> " + entry.ipAddress);
                return entry.ipAddress;
            }

            else {
                System.out.println("Cache EXPIRED -> " + domain);
                cache.remove(domain);
            }
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 10)); // TTL = 10 seconds

        System.out.println("Cache MISS -> Query upstream -> " + ip);

        return ip;
    }

    // Simulated DNS lookup
    private String queryUpstreamDNS(String domain) {

        Random r = new Random();
        return "172.217.14." + (200 + r.nextInt(50));
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = (total == 0) ? 0 : (hits * 100.0 / total);

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws Exception {

        Problem3 dnsCache = new Problem3(5);

        dnsCache.resolve("google.com");

        Thread.sleep(2000);

        dnsCache.resolve("google.com");

        Thread.sleep(11000);

        dnsCache.resolve("google.com");

        dnsCache.getCacheStats();
    }
}