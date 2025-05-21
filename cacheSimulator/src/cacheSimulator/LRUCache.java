package cacheSimulator;
import java.util.*;

class LRUCache {
    public final Map<Integer, Node> map;
    final Node head;
    public final Node tail;
    public final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(-1, "HEAD");
        this.tail = new Node(-1, "TAIL");
        head.next = tail;
        tail.prev = head;
    }
    
    public boolean containsKey(int key) {
        return map.containsKey(key);
    }


    public CacheResult get(int key) {
        if (!MainMemory.isValid(key)) return new CacheResult(false, "Invalid Address");

        if (map.containsKey(key)) {
            Node node = map.get(key);
            deleteNode(node);
            insertNode(node);
            return new CacheResult(true, node.value);
        }

        String value = MainMemory.getValueAt(key);
        put(key, value);
        return new CacheResult(false, value);
    }
    public boolean isFull() {
        return map.size() == capacity;
    }

    public void put(int key, String value) {
        if (!MainMemory.isValid(key)) return;

        if (map.containsKey(key)) {
            Node node = map.get(key);
            deleteNode(node);
            node.value = value;
            insertNode(node);
        } else {
            if (map.size() == capacity) {
                deleteLast();
            }
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            insertNode(newNode);
        }
    }

    public void insertNode(Node node) {
        Node nextNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = nextNode;
        nextNode.prev = node;
    }

    public void deleteNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public void deleteLast() {
        Node lastNode = tail.prev;
        if (lastNode == head) return;
        MainMemory.setValueAt(lastNode.key, lastNode.value);
        deleteNode(lastNode);
        map.remove(lastNode.key);
    }

    static class Node {
        int key;
        String value;
        Node prev, next;

        Node(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
