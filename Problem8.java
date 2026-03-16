// File: Problem8.java
import java.util.*;

public class Problem8 {

    // -----------------------------
    // Parking Spot Status
    // -----------------------------
    enum SpotStatus { EMPTY, OCCUPIED, DELETED }

    static class ParkingSpot {
        String licensePlate;
        SpotStatus status;
        long entryTime; // in milliseconds

        ParkingSpot() {
            this.status = SpotStatus.EMPTY;
        }
    }

    // -----------------------------
    // Parking Lot Class
    // -----------------------------
    private final ParkingSpot[] spots;
    private final int capacity;
    private int totalProbes = 0;
    private int totalParked = 0;
    private Map<Integer, Integer> hourlyOccupancy = new HashMap<>(); // hour -> occupancy count

    public Problem8(int capacity) {
        this.capacity = capacity;
        spots = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new ParkingSpot();
        }
    }

    // -----------------------------
    // Custom hash function
    // -----------------------------
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // -----------------------------
    // Park vehicle using linear probing
    // -----------------------------
    public void parkVehicle(String licensePlate) {
        int hash = hash(licensePlate);
        int probes = 0;

        while (probes < capacity) {
            int spotIndex = (hash + probes) % capacity;
            if (spots[spotIndex].status == SpotStatus.EMPTY || spots[spotIndex].status == SpotStatus.DELETED) {
                spots[spotIndex].licensePlate = licensePlate;
                spots[spotIndex].status = SpotStatus.OCCUPIED;
                spots[spotIndex].entryTime = System.currentTimeMillis();
                totalProbes += probes;
                totalParked++;
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                hourlyOccupancy.put(hour, hourlyOccupancy.getOrDefault(hour, 0) + 1);
                System.out.println("parkVehicle(\"" + licensePlate + "\") → Assigned spot #" + spotIndex + " (" + probes + " probes)");
                return;
            }
            probes++;
        }

        System.out.println("Parking Lot Full! Cannot park " + licensePlate);
    }

    // -----------------------------
    // Exit vehicle and calculate fee
    // -----------------------------
    public void exitVehicle(String licensePlate) {
        for (int i = 0; i < capacity; i++) {
            if (spots[i].status == SpotStatus.OCCUPIED && spots[i].licensePlate.equals(licensePlate)) {
                long durationMs = System.currentTimeMillis() - spots[i].entryTime;
                double hours = durationMs / 3600000.0; // convert ms to hours
                double fee = Math.ceil(hours) * 5.0; // $5 per hour, rounded up
                spots[i].status = SpotStatus.DELETED;
                totalParked--;
                System.out.printf("exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2f h, Fee: $%.2f%n", licensePlate, i, hours, fee);
                return;
            }
        }
        System.out.println("Vehicle " + licensePlate + " not found in parking lot.");
    }

    // -----------------------------
    // Parking statistics
    // -----------------------------
    public void getStatistics() {
        double occupancy = totalParked * 100.0 / capacity;
        double avgProbes = totalParked == 0 ? 0 : totalProbes * 1.0 / totalParked;

        // Find peak hour
        int peakHour = -1, peakCount = 0;
        for (Map.Entry<Integer, Integer> entry : hourlyOccupancy.entrySet()) {
            if (entry.getValue() > peakCount) {
                peakCount = entry.getValue();
                peakHour = entry.getKey();
            }
        }

        System.out.printf("getStatistics() → Occupancy: %.1f%%, Avg Probes: %.2f, Peak Hour: %d:00 - %d:00%n",
                occupancy, avgProbes, peakHour, (peakHour + 1) % 24);
    }

    // -----------------------------
    // Main method to simulate parking
    // -----------------------------
    public static void main(String[] args) throws InterruptedException {
        Problem8 lot = new Problem8(500);

        // Simulate parking vehicles
        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000); // simulate 2 seconds parking

        // Exit a vehicle
        lot.exitVehicle("ABC-1234");

        // Display statistics
        lot.getStatistics();
    }
}