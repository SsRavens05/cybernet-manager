package com.example.cybergame_management;

final class SearchMatcher {
    private SearchMatcher() {
    }

    static boolean containsKeyword(String keyword, String... values) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        for (String value : values) {
            if (value != null && value.toLowerCase().contains(normalizedKeyword)) {
                return true;
            }
        }
        return false;
    }
}
