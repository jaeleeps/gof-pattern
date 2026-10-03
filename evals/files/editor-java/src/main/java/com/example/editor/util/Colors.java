package com.example.editor.util;

public final class Colors {
    private Colors() { }

    public static boolean isHex(String s) {
        return s != null && s.matches("#[0-9a-fA-F]{6}");
    }

    public static String darken(String hex, double factor) {
        int rgb = Integer.parseInt(hex.substring(1), 16);
        int r = (int) (((rgb >> 16) & 0xff) * factor);
        int g = (int) (((rgb >> 8) & 0xff) * factor);
        int b = (int) ((rgb & 0xff) * factor);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
