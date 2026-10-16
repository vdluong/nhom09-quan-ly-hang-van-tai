package utils;

public final class Validator {
    private Validator() {}

    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
