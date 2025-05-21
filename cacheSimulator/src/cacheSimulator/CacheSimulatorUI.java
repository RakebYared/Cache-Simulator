package cacheSimulator;
import java.util.List;
import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;


public class CacheSimulatorUI extends JFrame {
    private JTextField capacityField;
    private JRadioButton fifoButton, lruButton, lfuButton;
    private int capacity;
    private String policy;
    private Object cache;
    private JTable memoryTable;
    private JPanel cachePanel;
    private JLabel resultLabel;
    private JTextField getKeyField, putKeyField, putValueField;

    public CacheSimulatorUI() {
        setTitle("Cache Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/cacheicon.png"));
            setIconImage(image.getImage());
        } catch (Exception e) {
            System.err.println("Icon not found: " + e.getMessage());
        }
        setLocationRelativeTo(null);
//      setResizable(false);
        showHomeWindow();
    }

    private void showHomeWindow() {
        getContentPane().removeAll();

        // Main panel with vertical box layout and background color
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));
        panel.setBackground(new Color(240, 248, 255)); // Light blue background (AliceBlue)
        panel.setOpaque(true);

        // Spacer to push content toward center
        panel.add(Box.createVerticalGlue());

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to Cache Simulator", SwingConstants.CENTER);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        welcomeLabel.setForeground(new Color(25, 25, 112)); // Dark blue text
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(30));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false); // Transparent so background shows
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel capacityLabel = new JLabel("Cache Capacity (1-10):");
        capacityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        capacityField = new JTextField(2);
        capacityField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(capacityLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(capacityField, gbc);

        JLabel policyLabel = new JLabel("Cache Policy:");
        policyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JPanel policyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        policyPanel.setOpaque(false);
        fifoButton = new JRadioButton("FIFO", true);
        lruButton = new JRadioButton("LRU");
        lfuButton = new JRadioButton("LFU");
        fifoButton.setFont(new Font("Arial", Font.PLAIN, 14));
        lruButton.setFont(new Font("Arial", Font.PLAIN, 14));
        lfuButton.setFont(new Font("Arial", Font.PLAIN, 14));
        ButtonGroup policyGroup = new ButtonGroup();
        policyGroup.add(fifoButton);
        policyGroup.add(lruButton);
        policyGroup.add(lfuButton);
        policyPanel.add(fifoButton);
        policyPanel.add(lruButton);
        policyPanel.add(lfuButton);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(policyLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(policyPanel, gbc);

        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(25));

        // Start button
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(new Color(30, 144, 255)); 
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startSimulation());
        panel.add(startButton);

        panel.add(Box.createVerticalGlue());

        add(panel);
        revalidate();
        repaint();
    }


    private void startSimulation() {
        try {
            capacity = Integer.parseInt(capacityField.getText());
            if (capacity < 1 || capacity > 10) {
                JOptionPane.showMessageDialog(this, "Capacity must be between 1 and 10", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fifoButton.isSelected()) {
            policy = "FIFO";
            cache = new FIFOCache(capacity);
        } else if (lruButton.isSelected()) {
            policy = "LRU";
            cache = new LRUCache(capacity);
        } else {
            policy = "LFU";
            cache = new LFUCache(capacity);
        }

        showCacheWindow();
    }

    private void showCacheWindow() {
        getContentPane().removeAll();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 248, 255)); 

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(100, 149, 237));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(new Color(245, 248, 255));
        backPanel.add(backButton);
        backButton.addActionListener(e -> showHomeWindow());
        mainPanel.add(backPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 248, 255)); // match main panel
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        // Cache Section
        JPanel cacheSection = new JPanel(new BorderLayout());
        cacheSection.setBorder(BorderFactory.createTitledBorder("Cache (" + policy + ")"));
        cacheSection.setBackground(Color.WHITE);

        cachePanel = new JPanel(new GridBagLayout());
        cachePanel.setBackground(Color.WHITE);
        JScrollPane cacheScroll = new JScrollPane(cachePanel);
        cacheScroll.setPreferredSize(new Dimension(320, 240));
        updateCacheInterface();
        cacheSection.add(cacheScroll, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 60));
        resultLabel.setForeground(new Color(25, 25, 112)); // Navy
        bottomPanel.add(resultLabel, BorderLayout.NORTH);

        JPanel operationPanel = new JPanel(new GridLayout(1, 2));
        operationPanel.setBackground(Color.WHITE);

        // Get Panel
        JPanel getPanel = new JPanel(new FlowLayout());
        getPanel.setBorder(BorderFactory.createTitledBorder("Get"));
        getPanel.setBackground(Color.WHITE);
        getKeyField = new JTextField(3);
        JButton getButton = new JButton("Get");
        getButton.setBackground(new Color(65, 105, 225)); // Royal Blue
        getButton.setForeground(Color.WHITE);
        getButton.setFocusPainted(false);
        getButton.addActionListener(e -> performGet());
        getPanel.add(new JLabel("Key:"));
        getPanel.add(getKeyField);
        getPanel.add(getButton);

        // Put Panel
        JPanel putPanel = new JPanel(new FlowLayout());
        putPanel.setBorder(BorderFactory.createTitledBorder("Put"));
        putPanel.setBackground(Color.WHITE);
        putKeyField = new JTextField(3);
        putValueField = new JTextField(3);
        JButton putButton = new JButton("Put");
        putButton.setBackground(new Color(34, 139, 34)); // Forest Green
        putButton.setForeground(Color.WHITE);
        putButton.setFocusPainted(false);
        putButton.addActionListener(e -> performPut());
        putPanel.add(new JLabel("Key:"));
        putPanel.add(putKeyField);
        putPanel.add(new JLabel("Value:"));
        putPanel.add(putValueField);
        putPanel.add(putButton);

        operationPanel.add(getPanel);
        operationPanel.add(putPanel);
        bottomPanel.add(operationPanel, BorderLayout.CENTER);

        cacheSection.add(bottomPanel, BorderLayout.SOUTH);

        c.gridx = 0;
        c.weightx = 0.4;
        contentPanel.add(cacheSection, c);

        // Memory Section
        JPanel memorySection = new JPanel(new BorderLayout());
        memorySection.setBorder(BorderFactory.createTitledBorder("Main Memory"));
        memorySection.setBackground(Color.WHITE);
        String[] columnNames = {"Address", "Value"};
        Object[][] data = new Object[17][2];
        for (int i = 0; i < 17; i++) {
            data[i][0] = MainMemory.memory[i][0];
            data[i][1] = MainMemory.memory[i][1];
        }
        memoryTable = new JTable(data, columnNames);
        memoryTable.setEnabled(false);
        JScrollPane memoryScroll = new JScrollPane(memoryTable);
        memorySection.add(memoryScroll, BorderLayout.CENTER);

        c.gridx = 1;
        c.weightx = 0.6;
        contentPanel.add(memorySection, c);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        revalidate();
        repaint();
    }

    private void updateMainMemoryInterface() {
        for (int i = 0; i < 17; i++) {
            memoryTable.setValueAt(MainMemory.memory[i][1], i, 1);
        }
    }
    
    private JPanel createCacheBox(int key, String value) {
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(60, 50));
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        box.setLayout(new GridLayout(2, 1));
        box.add(new JLabel("Key: " + key, SwingConstants.CENTER));
        box.add(new JLabel("Val: " + value, SwingConstants.CENTER));
        return box;
    }


    private void updateCacheInterface() {
        cachePanel.removeAll();
        cachePanel.setLayout(new BoxLayout(cachePanel, BoxLayout.Y_AXIS));

        if (policy.equals("FIFO")) {
            FIFOCache fifo = (FIFOCache) cache;
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

            int size = fifo.queue.size();
            int index = 0;
            for (FIFOCache.Node node : fifo.queue) {
                rowPanel.add(createCacheBox(node.key, node.value));
                if (++index < size) {
                    rowPanel.add(new JLabel("-"));
                }
            }
            cachePanel.add(rowPanel);

        } else if (policy.equals("LRU")) {
            LRUCache lru = (LRUCache) cache;
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

            List<LRUCache.Node> nodes = new ArrayList<>();
            LRUCache.Node current = lru.head.next;
            while (current != lru.tail) {
                nodes.add(current);
                current = current.next;
            }

            for (int i = 0; i < nodes.size(); i++) {
                rowPanel.add(createCacheBox(nodes.get(i).key, nodes.get(i).value));
                if (i < nodes.size() - 1) {
                    rowPanel.add(new JLabel("<->"));
                }
            }
            cachePanel.add(rowPanel);

        } else if (policy.equals("LFU")) {
            LFUCache lfu = (LFUCache) cache;

            Map<Integer, List<LFUCache.Node>> grouped = new TreeMap<>();
            for (LFUCache.Node node : lfu.cache.values()) {
                grouped.computeIfAbsent(node.frequency, k -> new ArrayList<>()).add(node);
            }

            for (Map.Entry<Integer, List<LFUCache.Node>> entry : grouped.entrySet()) {
                int freq = entry.getKey();
                List<LFUCache.Node> nodes = entry.getValue();

                JLabel freqLabel = new JLabel("Freq " + freq + ": ");
                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                rowPanel.add(freqLabel);

                for (int i = 0; i < nodes.size(); i++) {
                    rowPanel.add(createCacheBox(nodes.get(i).key, nodes.get(i).val));
                    if (i < nodes.size() - 1) {
                        rowPanel.add(new JLabel("<-"));
                    }
                }
                cachePanel.add(rowPanel);
            }
        }

        cachePanel.revalidate();
        cachePanel.repaint();
    }


//    private JPanel createCacheCell(int key, String value) {
//        JPanel panel = new JPanel(new GridLayout(2, 1));
//        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        panel.add(new JLabel("Key: " + key, SwingConstants.CENTER));
//        panel.add(new JLabel("Val: " + value, SwingConstants.CENTER));
//        return panel;
//    }


    private void performGet() {
        try {
            int key = Integer.parseInt(getKeyField.getText());
            CacheResult result;

            if (policy.equals("FIFO")) {
                result = ((FIFOCache) cache).get(key);
            } else if (policy.equals("LRU")) {
                result = ((LRUCache) cache).get(key);
            } else {
                result = ((LFUCache) cache).get(key);
            }

            resultLabel.setText(result.toString());
            resultLabel.setForeground(result.isHit ? Color.GREEN.darker() : Color.RED);
            updateMainMemoryInterface();
            updateCacheInterface();
            clearTextFields();
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid Key");
            resultLabel.setForeground(Color.RED);
        }
    }


    private void performPut() {
        try {
            int key = Integer.parseInt(putKeyField.getText());
            String value = putValueField.getText();

            if (value.isEmpty()) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Value cannot be empty");
                return;
            }

            boolean isHit = false;
            boolean wasFullBefore = false;

            if (fifoButton.isSelected()) {
                FIFOCache fifo = (FIFOCache) cache;
                isHit = fifo.containsKey(key);
                wasFullBefore = !isHit && fifo.isFull();  // Check BEFORE insertion
                fifo.put(key, value);
            } else if (lruButton.isSelected()) {
                LRUCache lru = (LRUCache) cache;
                isHit = lru.containsKey(key);
                wasFullBefore = !isHit && lru.isFull();
                lru.put(key, value);
            } else if (lfuButton.isSelected()) {
                LFUCache lfu = (LFUCache) cache;
                isHit = lfu.containsKey(key);
                wasFullBefore = !isHit && lfu.isFull();
                lfu.put(key, value);
            }

            if (wasFullBefore) {
                JOptionPane.showMessageDialog(this, "Cache is full! An item may have been evicted.", "Cache Full", JOptionPane.WARNING_MESSAGE);
            }

            resultLabel.setForeground(isHit ? Color.GREEN : Color.RED);
            resultLabel.setText((isHit ? "[hit]" : "[miss]") + " updated!");

            updateMainMemoryInterface();
            updateCacheInterface();
            clearTextFields();

        } catch (NumberFormatException e) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Invalid Key");
        }
    }


    private void clearTextFields() {
        getKeyField.setText("");
        putKeyField.setText("");
        putValueField.setText("");
    }

 
}