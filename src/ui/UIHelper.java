package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class UIHelper {

    // === BẢNG MÀU CHUẨN MODERN SLATE / NAVY ===
    public static final Color SIDEBAR_BG     = new Color(15, 23, 42);    // #0F172A Dark Slate Navy
    public static final Color SIDEBAR_ACTIVE = new Color(30, 41, 59);    // #1E293B Slate Active
    public static final Color MAIN_BG        = new Color(248, 250, 252); // #F8FAFC Background
    public static final Color CARD_BG        = Color.WHITE;               // #FFFFFF Nền Card
    public static final Color BORDER_COLOR   = new Color(226, 232, 240); // #E2E8F0 Viền xám nhạt

    // === MÀU BẢNG & NÚT CHỨC NĂNG ===
    public static final Color TABLE_HEADER_BG = new Color(20, 50, 90);   // Xanh Navy Đậm Header Bảng
    public static final Color BTN_GREEN       = new Color(22, 163, 74);   // #16A34A (Lưu mới, Excel)
    public static final Color BTN_BLUE        = new Color(2, 132, 199);   // #0284C7 (Cập nhật)
    public static final Color BTN_RED         = new Color(220, 38, 38);   // #DC2626 (Xóa)
    public static final Color BTN_GRAY        = new Color(100, 116, 139); // #64748B (Làm mới)

    /**
     * Hiển thị hộp thoại thông báo lỗi ràng buộc CSDL hoặc dữ liệu nhập
     */
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Cảnh báo Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Hiển thị hộp thoại thông báo thành công
     */
    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Nạp ảnh từ thư mục nguồn src và co dãn (resize) mượt mà về kích thước mong muốn
     * @param resourcePath Đường dẫn file tính từ gốc src (Ví dụ: "/icons/bus.png")
     * @param width Chiều rộng pixel
     * @param height Chiều cao pixel
     */
    public static ImageIcon getIcon(String resourcePath, int width, int height) {
        try {
            URL imgURL = UIHelper.class.getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                Image scaledImg = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } else {
                System.err.println("Lỗi: Không tìm thấy file ảnh tại " + resourcePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi nạp ảnh: " + e.getMessage());
            return null;
        }
    }
}