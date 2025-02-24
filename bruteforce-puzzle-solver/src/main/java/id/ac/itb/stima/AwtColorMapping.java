package id.ac.itb.stima;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class AwtColorMapping {
    private static final Map<Character, Color> colorMap = new HashMap<>();
    private static final Color[] colorPalette = {
        new Color(255, 0, 0),     
        new Color(0, 255, 0),   
        new Color(0, 0, 255),   
        new Color(255, 165, 0), 
        new Color(128, 0, 128), 
        new Color(139, 69, 19), 
        new Color(255, 255, 0), 
        new Color(255, 192, 203),
        new Color(0, 255, 255), 
        new Color(255, 0, 255), 
        new Color(169, 169, 169),
        new Color(173, 216, 230),
        new Color(50, 205, 50), 
        new Color(139, 0, 0),   
        new Color(0, 128, 128), 
        new Color(255, 215, 0), 
        new Color(238, 130, 238),
        new Color(0, 0, 128),   
        new Color(240, 230, 140),
        new Color(221, 160, 221),
        new Color(75, 0, 130),  
        new Color(250, 128, 114),
        new Color(255, 99, 71), 
        new Color(0, 191, 255), 
        new Color(0, 255, 127),    
        new Color(160, 82, 45),
    };

    static {
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            colorMap.put(letter, colorPalette[i % colorPalette.length]); 
        }
        colorMap.put('\0', new Color(211, 211, 211));
    }

    public static Color getAwtColor(char c) {
        return colorMap.getOrDefault(c, Color.BLACK);
    }

    public static void main(String[] args) {
        for (char c = 'A'; c <= 'Z'; c++) {
            System.out.println("Character " + c + " -> Color " + getAwtColor(c));
        }
    }
}
