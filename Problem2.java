import java.util.*;

public class Problem2 {

    // Stores product stock
    HashMap<String, Integer> inventory = new HashMap<>();

    // Waiting list for each product
    HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();


    // Add product to inventory
    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }


    // Check stock availability
    public int checkStock(String productId) {

        if (!inventory.containsKey(productId)) {
            System.out.println("Product not found");
            return -1;
        }

        int stock = inventory.get(productId);

        System.out.println(productId + " → " + stock + " units available");

        return stock;
    }


    // Purchase item (thread safe)
    public synchronized void purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        // If stock available
        if (stock > 0) {

            inventory.put(productId, stock - 1);

            System.out.println(
                    "User " + userId + " purchase success. Remaining stock: "
                            + (stock - 1)
            );

        } else {

            // Add user to waiting list
            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;

            queue.put(userId, position);

            System.out.println(
                    "User " + userId + " added to waiting list. Position #" + position
            );
        }
    }


    // Show waiting list
    public void showWaitingList(String productId) {

        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("Waiting list empty.");
            return;
        }

        System.out.println("Waiting List:");

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {

            System.out.println(
                    "User " + entry.getKey() + " → Position " + entry.getValue()
            );
        }
    }


    public static void main(String[] args) {

        Problem2 system = new Problem2();

        // Add product
        system.addProduct("IPHONE15_256GB", 5);

        // Check stock
        system.checkStock("IPHONE15_256GB");

        // Simulate purchases
        system.purchaseItem("IPHONE15_256GB", 101);
        system.purchaseItem("IPHONE15_256GB", 102);
        system.purchaseItem("IPHONE15_256GB", 103);
        system.purchaseItem("IPHONE15_256GB", 104);
        system.purchaseItem("IPHONE15_256GB", 105);

        // Stock finished
        system.purchaseItem("IPHONE15_256GB", 106);
        system.purchaseItem("IPHONE15_256GB", 107);

        // Show waiting list
        system.showWaitingList("IPHONE15_256GB");
    }
}