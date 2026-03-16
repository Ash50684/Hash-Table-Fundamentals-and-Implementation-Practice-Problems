import java.util.*;

// Trie Node
class TrieNodee {
    Map<Character, TrieNodee> children = new HashMap<>();
    Map<String, Integer> queryFrequency = new HashMap<>();
}

// Main class
public class Problem7 {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> globalFrequency = new HashMap<>();

    // Add or update a query
    public void addQuery(String query) {

        int frequency = globalFrequency.getOrDefault(query, 0) + 1;
        globalFrequency.put(query, frequency);

        TrieNode current = root;

        for (char ch : query.toCharArray()) {

            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);

            current.queryFrequency.put(query, frequency);
        }
    }

    // Search top 10 suggestions for a prefix
    public List<String> search(String prefix) {

        TrieNode current = root;

        // Traverse the trie
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return new ArrayList<>();
            }
            current = current.children.get(ch);
        }

        // Min-heap to keep top 10 results
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : current.queryFrequency.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        // Convert heap to list
        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            result.add(entry.getKey() + " (" + entry.getValue() + ")");
        }

        Collections.reverse(result); // Highest frequency first
        return result;
    }

    // Driver code
    public static void main(String[] args) {

        Problem7 system = new Problem7();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");

        List<String> suggestions = system.search("jav");

        System.out.println("Search Suggestions:");

        for (String suggestion : suggestions) {
            System.out.println(suggestion);
        }
    }
}