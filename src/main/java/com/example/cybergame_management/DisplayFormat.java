package com.example.cybergame_management;

import java.text.NumberFormat;
import java.util.Locale;

final class DisplayFormat {
    private DisplayFormat() {
    }

    static int parseInt(String value) {
        if (value == null) {
            return 0;
        }
        String digits = value.replaceAll("[^0-9-]", "");
        if (digits.isEmpty() || digits.equals("-")) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static long parseMoney(String value) {
        if (value == null) {
            return 0;
        }
        String digits = value.replaceAll("[^0-9-]", "");
        if (digits.isEmpty() || digits.equals("-")) {
            return 0;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static String money(long amount) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + "đ";
    }
}
