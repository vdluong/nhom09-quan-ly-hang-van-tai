package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class DashboardPanel extends JPanel {

    private Consumer<String> onNavigate;

    public DashboardPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249)); // Màu nền Slate-100 xám nhạt

        // === TIÊU ĐỀ TRONG BẢNG ===
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 6));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(45, 20, 25, 20));

        JLabel lblTitle = new JLabel("DANH MỤC QUẢN LÝ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Hệ thống quản lý và vận tải hành khách", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);
        add(titlePanel, BorderLayout.NORTH);

        // === 4 THẺ DẠNG NẰM HÀNG NGANG (1 HÀNG X 4 CỘT) ===
        JPanel gridContainer = new JPanel(new GridBagLayout());
        gridContainer.setOpaque(false);

        // Lưới 1 hàng, 4 cột chuẩn theo ảnh mẫu
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(900, 360)); // Kích thước khung chứa 4 thẻ cao đứng

        // Tạo 4 thẻ đứng tương ứng 4 phân hệ
        cardsGrid.add(createVerticalCard("XE KHÁCH", "🚍", new Color(199, 210, 234), "XE_KHACH"));
        cardsGrid.add(createVerticalCard("TÀI XẾ", "👨‍✈️", new Color(195, 216, 202), "TAI_XE"));
        cardsGrid.add(createVerticalCard("TUYẾN XE", "🗺️", new Color(237, 221, 197), "TUYEN_XE"));
        cardsGrid.add(createVerticalCard("CHUYẾN XE", "📅", new Color(208, 199, 227), "CHUYEN_XE"));

        gridContainer.add(cardsGrid);
        add(gridContainer, BorderLayout.CENTER);
    }

    // Hàm vẽ 1 Thẻ đứng (Vertical Card) bo góc chuẩn theo ảnh mẫu
    private JPanel createVerticalCard(String title, String iconSymbol, Color accentColor, String cardKey) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // 1. Tô kín toàn bộ ô bằng màu sắc chủ đạo (hoặc dùng màu nhạt tuỳ ý)
                g2.setColor(accentColor); // Hoặc bạn có thể dùng màu pastel nhạt riêng cho từng ô
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));

                // 2. Viền khung bên ngoài (nếu muốn)
                g2.setColor(new Color(200, 210, 225));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 16, 16));

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(25, 15, 15, 15));

        // Phần giữa: Icon lớn & Tiêu đề
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 18, 0);

        JLabel lblIcon = new JLabel(iconSymbol, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 54));
        centerPanel.add(lblIcon, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));
        centerPanel.add(lblTitle, gbc);

        // Góc dưới bên phải: Mũi tên →
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);

        JLabel lblArrow = new JLabel("→");
        lblArrow.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblArrow.setForeground(new Color(148, 163, 184)); // Màu xám nhạt
        bottomPanel.add(lblArrow);

        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        // Bắt sự kiện Click vào Thẻ
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onNavigate != null) {
                    onNavigate.accept(cardKey);
                }
            }
        });

        return card;
    }
}
