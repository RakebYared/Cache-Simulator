package cacheSimulator;

public class MainMemory {
    static final Object[][] memory = new Object[17][2];

    static {
        for (int i = 0; i <= 16; i++) {
            memory[i][0] = i;
            memory[i][1] = String.valueOf((char) ('A' + i));
        }
    }

    public static boolean isValid(int address) {
        return address >= 0 && address < memory.length;
    }

    public static String getValueAt(int address) {
        return (String) memory[address][1];
    }

    public static void setValueAt(int address, String value) {
        memory[address][1] = value;
    }
}

