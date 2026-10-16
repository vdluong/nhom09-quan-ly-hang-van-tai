package ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Quản lý hàng vận tải - Nhóm 09");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new JLabel("Hệ thống quản lý hàng vận tải", SwingConstants.CENTER));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
