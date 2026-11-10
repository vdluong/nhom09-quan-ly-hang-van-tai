
package utils;

public final class Validator {
    private Validator() {}

    // Kiểm tra dữ liệu không được để trống
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Kiểm tra số nguyên dương
    public static boolean isPositive(int value) {
        return value > 0;
    }

    // Kiểm tra số ghế hợp lệ
    public static boolean isValidSoGhe(int soGhe) {
        return soGhe >= 4;
    }

    // Kiểm tra năm sản xuất hợp lệ
    public static boolean isValidNam(int nam) {
        return nam > 0 && nam <= java.time.Year.now().getValue();
    }

    // Kiểm tra biển số xe không được để trống
    public static boolean isValidBienSo(String bienSo) {
        return isNotBlank(bienSo);
    }
}
