import java.util.*;

public class Problem1 {

    HashMap<String, Integer> usernameMap = new HashMap<>();
    HashMap<String, Integer> attemptFrequency = new HashMap<>();


    // Check if username is available
    public boolean checkAvailability(String username) {

        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }


    // Register user
    public void registerUser(String username, int userId) {
        usernameMap.put(username, userId);
        System.out.println("User registered: " + username);
    }


    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String newName = username + i;

            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        String dotVersion = username.replace("_", ".");
        if (!usernameMap.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }


    // Get most attempted username
    public String getMostAttempted() {

        String most = "";
        int max = 0;

        for (String key : attemptFrequency.keySet()) {

            int count = attemptFrequency.get(key);

            if (count > max) {
                max = count;
                most = key;
            }
        }

        return most + " (" + max + " attempts)";
    }


    public static void main(String[] args) {

        Problem1 system = new Problem1();

        system.registerUser("john_doe", 1001);
        system.registerUser("admin", 1002);

        System.out.println("john_doe available? " +
                system.checkAvailability("john_doe"));

        System.out.println("jane_smith available? " +
                system.checkAvailability("jane_smith"));

        System.out.println("Suggestions: " +
                system.suggestAlternatives("john_doe"));

        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println("Most attempted username: " +
                system.getMostAttempted());
    }
}