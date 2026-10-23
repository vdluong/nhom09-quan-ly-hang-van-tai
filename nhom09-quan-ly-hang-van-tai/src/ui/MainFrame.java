package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private DashboardPanel dashboardPanel;
    private QuanLyCRUDPanel crudPanel;
    private TruyVanThongKePanel truyVanPanel;

    public MainFrame() {
        setTitle("QUẢN LÝ ĐỘI XE");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // === THANH TOPBAR MÀU TRẮNG TINH TẾ (ĐÃ XÓA NÚT TRANG CHỦ) ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Bên trái TopBar: Icon Bus + Title
        JPanel leftBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBrand.setOpaque(false);

        JLabel lblBusIcon = new JLabel("");
        lblBusIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel lblBrandTitle = new JLabel("QUẢN LÝ ĐỘI XE");
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBrandTitle.setForeground(new Color(30, 41, 59));

        leftBrand.add(lblBusIcon);
        leftBrand.add(lblBrandTitle);

        // Bên phải TopBar: BÁO CÁO (TRUY VẤN), Bell, Icon User
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        rightNav.setOpaque(false);

        JButton btnTruyVan = new JButton("BÁO CÁO");
        btnTruyVan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTruyVan.setForeground(new Color(71, 85, 105));
        btnTruyVan.setContentAreaFilled(false);
        btnTruyVan.setBorderPainted(false);
        btnTruyVan.setFocusPainted(false);
        btnTruyVan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblBellIcon = new JLabel("🔔");
        lblBellIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblBellIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblUserIcon = new JLabel("👤");
        lblUserIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblUserIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightNav.add(btnTruyVan);
        rightNav.add(lblBellIcon);
        rightNav.add(lblUserIcon);

        topBar.add(leftBrand, BorderLayout.WEST);
        topBar.add(rightNav, BorderLayout.EAST);

        // === CONTAINER CHÍNH CHUYỂN MÀN HÌNH ===
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Truyền callback `this::navigateTo` vào cả Dashboard và CRUD Panel
        dashboardPanel = new DashboardPanel(this::navigateTo);
        crudPanel = new QuanLyCRUDPanel(this::navigateTo);
        truyVanPanel = new TruyVanThongKePanel();

        mainContainer.add(dashboardPanel, "DASHBOARD");
        mainContainer.add(crudPanel, "CRUD");
        mainContainer.add(truyVanPanel, "TRUY_VAN");

        btnTruyVan.addActionListener(e -> cardLayout.show(mainContainer, "TRUY_VAN"));

        add(topBar, BorderLayout.NORTH);
        add(mainContainer, BorderLayout.CENTER);

        // Mặc định khởi chạy Dashboard 4 ô vuông
        cardLayout.show(mainContainer, "DASHBOARD");
    }

    public void navigateTo(String destination) {
        if ("DASHBOARD".equals(destination)) {
            cardLayout.show(mainContainer, "DASHBOARD");
        } else if ("TRUY_VAN".equals(destination)) {
            cardLayout.show(mainContainer, "TRUY_VAN");
        } else {
            crudPanel.showModule(destination);
            cardLayout.show(mainContainer, "CRUD");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
