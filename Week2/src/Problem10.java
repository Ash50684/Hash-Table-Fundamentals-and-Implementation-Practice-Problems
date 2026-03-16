import java.util.*;

// Video Data Model
class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class Problem10 {

    // Cache Capacities
    private static final int L1_CAPACITY = 10000;
    private static final int L2_CAPACITY = 100000;

    // L1 Cache (Memory - Fastest)
    private LinkedHashMap<String, VideoData> L1 =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > L1_CAPACITY;
                }
            };

    // L2 Cache (SSD - Medium speed)
    private LinkedHashMap<String, VideoData> L2 =
            new LinkedHashMap<>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > L2_CAPACITY;
                }
            };

    // L3 Storage (Database)
    private HashMap<String, VideoData> L3 = new HashMap<>();

    // Track video access frequency
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Cache statistics
    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;
    private int totalRequests = 0;

    // Constructor
    public Problem10() {

        // Simulating database with 1000 videos
        for (int i = 1; i <= 1000; i++) {
            String id = "video_" + i;
            L3.put(id, new VideoData(id, "VideoContent_" + i));
        }
    }

    // Fetch video from cache system
    public VideoData getVideo(String videoId) {

        totalRequests++;

        // Check L1 Cache
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            incrementAccess(videoId);
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // Check L2 Cache
        if (L2.containsKey(videoId)) {
            L2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);
            promoteToL1(video);

            incrementAccess(videoId);
            return video;
        }

        System.out.println("L2 Cache MISS");

        // Fetch from Database
        VideoData video = L3.get(videoId);

        if (video != null) {
            L3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            // Store in L2 cache
            L2.put(videoId, video);
            accessCount.put(videoId, 1);

            return video;
        }

        System.out.println("Video not found");
        return null;
    }

    // Increase access count
    private void incrementAccess(String videoId) {

        int count = accessCount.getOrDefault(videoId, 0) + 1;
        accessCount.put(videoId, count);

        // Promote to L1 if frequently accessed
        if (count > 5 && L2.containsKey(videoId)) {
            promoteToL1(L2.get(videoId));
        }
    }

    // Move video to L1 cache
    private void promoteToL1(VideoData video) {
        L1.put(video.videoId, video);
    }

    // Remove video from all cache levels
    public void invalidate(String videoId) {

        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    // Print cache performance statistics
    public void getStatistics() {

        double L1Rate = (L1Hits * 100.0) / totalRequests;
        double L2Rate = (L2Hits * 100.0) / totalRequests;
        double L3Rate = (L3Hits * 100.0) / totalRequests;
        double overallRate = ((L1Hits + L2Hits) * 100.0) / totalRequests;

        System.out.println("\nCache Statistics:");
        System.out.printf("L1 Hit Rate: %.2f%%\n", L1Rate);
        System.out.printf("L2 Hit Rate: %.2f%%\n", L2Rate);
        System.out.printf("L3 Hit Rate: %.2f%%\n", L3Rate);
        System.out.printf("Overall Cache Hit Rate: %.2f%%\n", overallRate);
    }

    // Driver program
    public static void main(String[] args) {

        Problem10 cache = new Problem10();

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}