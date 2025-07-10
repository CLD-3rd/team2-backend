package com.bootcamp.savemypodo.global.enums;

public enum SortType {
	LATEST("latest"),
    MOST_RESERVED("most-reserved"),
    MINE("my-reservations");

    private final String key;

    SortType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static SortType from(String input) {
        for (SortType type : values()) {
            if (type.key.equalsIgnoreCase(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid sort type: " + input);
    }
}
