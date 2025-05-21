package cacheSimulator;
import java.util.*;

class LFUCache {
    final int capacity;
    int curSize;
    int minFrequency;
    Map<Integer, Node> cache;
    Map<Integer, DoubleLinkedList> frequencyMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.curSize = 0;
        this.minFrequency = 0;
        this.cache = new HashMap<>();
        this.frequencyMap = new HashMap<>();
    }
    public boolean containsKey(int key) {
        return cache.containsKey(key);
    }


    public CacheResult get(int key) {
        if (!MainMemory.isValid(key)) return new CacheResult(false, "Invalid Address");

        Node curNode = cache.get(key);
        if (curNode == null) {
            String value = MainMemory.getValueAt(key);
            put(key, value);
            return new CacheResult(false, value);
        }
        updateNode(curNode);
        return new CacheResult(true, curNode.val);
    }

    public void put(int key, String value) {
        if (!MainMemory.isValid(key) || capacity == 0) return;

        if (cache.containsKey(key)) {
            Node curNode = cache.get(key);
            curNode.val = value;
            updateNode(curNode);
        } else {
            curSize++;
            if (curSize > capacity) {
                DoubleLinkedList minFreqList = frequencyMap.get(minFrequency);
                Node toRemove = minFreqList.tail.prev;
                MainMemory.setValueAt(toRemove.key, toRemove.val);
                cache.remove(toRemove.key);
                minFreqList.removeNode(toRemove);
                curSize--;
            }
            minFrequency = 1;
            Node newNode = new Node(key, value);
            DoubleLinkedList curList = frequencyMap.getOrDefault(1, new DoubleLinkedList());
            curList.addNode(newNode);
            frequencyMap.put(1, curList);
            cache.put(key, newNode);
        }
    }

    public void updateNode(Node curNode) {
        int curFreq = curNode.frequency;
        DoubleLinkedList curList = frequencyMap.get(curFreq);
        curList.removeNode(curNode);
        if (curFreq == minFrequency && curList.listSize == 0) {
            minFrequency++;
        }
        curNode.frequency++;
        DoubleLinkedList newList = frequencyMap.getOrDefault(curNode.frequency, new DoubleLinkedList());
        newList.addNode(curNode);
        frequencyMap.put(curNode.frequency, newList);
    }
    public boolean isFull() {
        return cache.size() == capacity;
    }


    public static class Node {
        int key;
        String val;
        int frequency;
        Node prev;
        Node next;

        public Node(int key, String val) {
            this.key = key;
            this.val = val;
            this.frequency = 1;
        }
    }

    public static class DoubleLinkedList {
        int listSize;
        Node head;
        Node tail;

        public DoubleLinkedList() {
            this.listSize = 0;
            this.head = new Node(0, "Head");
            this.tail = new Node(0, "Tail");
            head.next = tail;
            tail.prev = head;
        }

        public void addNode(Node curNode) {
            Node nextNode = head.next;
            curNode.next = nextNode;
            curNode.prev = head;
            head.next = curNode;
            nextNode.prev = curNode;
            listSize++;
        }

        public void removeNode(Node curNode) {
            Node prevNode = curNode.prev;
            Node nextNode = curNode.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            listSize--;
        }
    }
}
