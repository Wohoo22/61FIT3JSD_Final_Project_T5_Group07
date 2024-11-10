package org.example.models;

import java.awt.Color;

public enum Colors {
    RED(255, 0, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255),
    YELLOW(255, 255, 0),
    ORANGE(255, 165, 0),
    PURPLE(128, 0, 128),
    PINK(255, 192, 203),
    BROWN(165, 42, 42);

    private final Color awtColor;

    // Constructor to initialize the Color with RGB values
    Colors(int r, int g, int b) {
        this.awtColor = new Color(r, g, b);
    }

    // Getter method to retrieve the corresponding java.awt.Color
    public Color getAwtColor() {
        return awtColor;
    }

    // Getter method to retrieve the red component
    public int getR() {
        return awtColor.getRed();
    }

    // Getter method to retrieve the green component
    public int getG() {
        return awtColor.getGreen();
    }

    // Getter method to retrieve the blue component
    public int getB() {
        return awtColor.getBlue();
    }
}