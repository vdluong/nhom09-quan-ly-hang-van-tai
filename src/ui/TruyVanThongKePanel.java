package ui;

import javax.swing.*;
import java.awt.*;

public class TruyVanThongKePanel extends JPanel {

    public TruyVanThongKePanel() {
        setLayout(new BorderLayout());

        // Hiển thị thông báo chờ ghép nối
        JLabel lblNotice = new JLabel("PHÂN HỆ TRUY VẤN & THỐNG KÊ (Thằng Chó Sơn Lê Ném Vào Đây)", SwingConstants.CENTER);
        lblNotice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNotice.setForeground(new Color(100, 116, 139));

        add(lblNotice, BorderLayout.CENTER);
    }
}