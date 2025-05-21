package cacheSimulator;
import java.util.*;

class FIFOCache {
    private final int capacity;
    private final Map<Integer, Node> map;
    public final Queue<Node> queue;

    public FIFOCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.queue = new LinkedList<>();
    }

    public CacheResult get(int key) {
        if (!MainMemory.isValid(key)) return new CacheResult(false, "Invalid Address");

        if (map.containsKey(key)) {
        	return new CacheResult(true, map.get(key).value);
        }

        String value = MainMemory.getValueAt(key);
        put(key, value);
        return new CacheResult(false, value);
    }

    public void put(int key, String value) {
        if (!MainMemory.isValid(key)) return;

        if (map.containsKey(key)) {
            map.get(key).value = value;
            return;
        }

        if (map.size() == capacity) {
            Node oldest = queue.poll();
            if (oldest != null) {
                MainMemory.setValueAt(oldest.key, oldest.value);
                map.remove(oldest.key);
            }
        }

        Node newNode = new Node(key, value);
        queue.offer(newNode);
        map.put(key, newNode);
    }
    public boolean isFull() {
        return map.size() == capacity;
    }


    public boolean containsKey(int key) {
        return map.containsKey(key);
    }

    static class Node {
        int key;
        String value;

        Node(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
