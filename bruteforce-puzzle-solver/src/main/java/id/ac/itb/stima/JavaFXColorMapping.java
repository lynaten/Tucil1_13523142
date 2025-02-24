package id.ac.itb.stima;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;


public class JavaFXColorMapping {
    private static final Map<Character, Color> fxColorMap = new HashMap<>();
    private static final Color[] fxColorPalette = {
        Color.RED, Color.GREEN, Color.BLUE,
        Color.ORANGE, Color.PURPLE, Color.BROWN,
        Color.YELLOW, Color.PINK, Color.CYAN,
        Color.MAGENTA, Color.DARKGRAY, Color.LIGHTBLUE,
        Color.LIME, Color.DARKRED, Color.TEAL,
        Color.GOLD, Color.VIOLET, Color.NAVY,
        Color.KHAKI, Color.PLUM, Color.INDIGO,
        Color.SALMON, Color.TOMATO, Color.DEEPSKYBLUE,
        Color.SPRINGGREEN, Color.SIENNA
    };

    static {
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            fxColorMap.put(letter, fxColorPalette[i % fxColorPalette.length]);
        }
        fxColorMap.put('\0', Color.LIGHTGRAY);
    }

    public static Color getFXColor(char c) {
        return fxColorMap.getOrDefault(c, Color.BLACK);
    }

    public static void main(String[] args) {
        for (char c = 'A'; c <= 'Z'; c++) {
            System.out.println("Character " + c + " -> JavaFX Color " + getFXColor(c));
        }
    }
}
