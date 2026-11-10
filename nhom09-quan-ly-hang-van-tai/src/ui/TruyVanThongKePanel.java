package ui;

import dao.ThongKeDAO;
import utils.ExportReportUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class TruyVanThongKePanel extends JPanel {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentCardLayout);
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");

    // Màu sắc đồng bộ với giao diện chính
    private static final Color SIDEBAR_BG = new Color(15, 23, 42);
    private static final Color SIDEBAR_ACTIVE = new Color(30, 41, 59);
    private static final Color CONTENT_BG = new Color(241, 245, 249);
    private static final Color PRIMARY_BLUE = new Color(2, 132, 199);
    private static final Color EXCEL_GREEN = new Color(22, 163, 74);
    private static final Color DANGER_RED = new Color(220, 38, 38);

    // Thành phần Truy vấn 1: Bảng lương
    private JSpinner spnThang;
    private JSpinner spnNam;
    private DefaultTableModel modelLuong;
    private JTable tblLuong;
    private JLabel lblTongLuong;

    // Thành phần Truy vấn 2: Doanh thu xe
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private DefaultTableModel modelDoanhThu;
    private JTable tblDoanhThu;
    private JLabel lblTongDoanhThu;

    // Thành phần Truy vấn 3A: Xe trong hạn bảo dưỡng
    private DefaultTableModel modelTrongHan;
    private JTable tblTrongHan;
    private JLabel lblSoXeTrongHan;

    // Thành phần Truy vấn 3B: Xe quá hạn bảo dưỡng
    private DefaultTableModel modelQuaHan;
    private JTable tblQuaHan;
    private JLabel lblSoXeQuaHan;

    public TruyVanThongKePanel() {
        setLayout(new BorderLayout());
        setBackground(CONTENT_BG);

        // 1. Khởi tạo thanh menu bên trái
        add(createSidebar(), BorderLayout.WEST);

        // 2. Khởi tạo 4 màn hình truy vấn bên phải
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.add(createBangLuongView(), "LUONG");
        contentPanel.add(createDoanhThuView(), "DOANH_THU");
        contentPanel.add(createXeTrongHanView(), "TRONG_HAN");
        contentPanel.add(createXeQuaHanView(), "QUA_HAN");

        add(contentPanel, BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        loadBangLuong();
        loadDoanhThu();
        loadXeTrongHan();
        loadXeQuaHan();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel lblTitle = new JLabel("BÁO CÁO & THỐNG KÊ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("PHÂN HỆ TRUY VẤN DỮ LIỆU");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblTitle);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(lblSub);
        sidebar.add(Box.createVerticalStrut(25));

        JButton btnMenuLuong = createMenuButton("Bảng lương tài xế", true);
        JButton btnMenuDoanhThu = createMenuButton("Doanh thu theo xe", false);
        JButton btnMenuTrongHan = createMenuButton("Xe trong hạn bảo dưỡng", false);
        JButton btnMenuQuaHan = createMenuButton("Xe quá hạn bảo dưỡng", false);
        JButton btnBack = createMenuButton("← Quay lại Quản lý", false);

        JButton[] menuButtons = {btnMenuLuong, btnMenuDoanhThu, btnMenuTrongHan, btnMenuQuaHan};

        btnMenuLuong.addActionListener(e -> {
            setActiveButton(menuButtons, btnMenuLuong);
            loadBangLuong();
            contentCardLayout.show(contentPanel, "LUONG");
        });

        btnMenuDoanhThu.addActionListener(e -> {
            setActiveButton(menuButtons, btnMenuDoanhThu);
            loadDoanhThu();
            contentCardLayout.show(contentPanel, "DOANH_THU");
        });

        btnMenuTrongHan.addActionListener(e -> {
            setActiveButton(menuButtons, btnMenuTrongHan);
            loadXeTrongHan();
            contentCardLayout.show(contentPanel, "TRONG_HAN");
        });

        btnMenuQuaHan.addActionListener(e -> {
            setActiveButton(menuButtons, btnMenuQuaHan);
            loadXeQuaHan();
            contentCardLayout.show(contentPanel, "QUA_HAN");
        });

        btnBack.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                ((CardLayout) parent.getLayout()).first(parent);
            }
        });

        sidebar.add(btnMenuLuong);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnMenuDoanhThu);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnMenuTrongHan);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnMenuQuaHan);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnBack);

        return sidebar;
    }

    private JButton createMenuButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        return btn;
    }

    private void setActiveButton(JButton[] allButtons, JButton activeBtn) {
        for (JButton btn : allButtons) {
            boolean isCurrent = (btn == activeBtn);
            btn.setBackground(isCurrent ? SIDEBAR_ACTIVE : SIDEBAR_BG);
            btn.setFont(new Font("Segoe UI", isCurrent ? Font.BOLD : Font.PLAIN, 13));
        }
    }

    // ================= 1. VIEW BẢNG LƯƠNG TÀI XẾ =================
    private JPanel createBangLuongView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Khung bộ lọc phía trên
        JPanel filterCard = createWhiteCard();
        filterCard.setLayout(new BorderLayout(0, 12));

        JLabel lblHeader = new JLabel("Thống kê Bảng lương Tài xế theo Tháng");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterCard.add(lblHeader, BorderLayout.NORTH);

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        formRow.setOpaque(false);

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        spnThang = new JSpinner(new SpinnerNumberModel(currentMonth, 1, 12, 1));
        spnThang.setPreferredSize(new Dimension(70, 32));

        spnNam = new JSpinner(new SpinnerNumberModel(currentYear, 2000, 2100, 1));
        JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(spnNam, "#");
        spnNam.setEditor(yearEditor);
        spnNam.setPreferredSize(new Dimension(90, 32));

        JButton btnFilter = createActionButton("Xem bảng lương", PRIMARY_BLUE);
        btnFilter.addActionListener(e -> loadBangLuong());

        lblTongLuong = new JLabel("Tổng quỹ lương: 0 VNĐ");
        lblTongLuong.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTongLuong.setForeground(PRIMARY_BLUE);

        formRow.add(new JLabel("Tháng:"));
        formRow.add(spnThang);
        formRow.add(new JLabel("Năm:"));
        formRow.add(spnNam);
        formRow.add(btnFilter);
        formRow.add(Box.createHorizontalStrut(25));
        formRow.add(lblTongLuong);

        filterCard.add(formRow, BorderLayout.CENTER);

        // Khung bảng dữ liệu phía dưới
        JPanel tableCard = createWhiteCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Danh sách Lương Tài xế");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btnExcel = createActionButton("📊 Excel", EXCEL_GREEN);
        tableTop.add(lblTableTitle, BorderLayout.WEST);
        tableTop.add(btnExcel, BorderLayout.EAST);

        String[] cols = {"STT", "Mã tài xế", "Họ và tên tài xế", "Lương tháng (VNĐ)"};
        modelLuong = createReadOnlyModel(cols);
        tblLuong = new JTable(modelLuong);
        styleTable(tblLuong);

        btnExcel.addActionListener(e -> ExportReportUtil.exportJTableToCSV(tblLuong, this));

        tableCard.add(tableTop, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(tblLuong), BorderLayout.CENTER);

        panel.add(filterCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadBangLuong() {
        int m = (int) spnThang.getValue();
        int y = (int) spnNam.getValue();
        List<Object[]> data = thongKeDAO.getBangLuong(m, y);

        modelLuong.setRowCount(0);
        BigDecimal tong = BigDecimal.ZERO;
        int stt = 1;

        for (Object[] row : data) {
            BigDecimal luong = row[2] instanceof BigDecimal ? (BigDecimal) row[2] : BigDecimal.ZERO;
            tong = tong.add(luong);
            modelLuong.addRow(new Object[]{
                    stt++,
                    row[0],
                    row[1],
                    currencyFormat.format(luong)
            });
        }
        lblTongLuong.setText("Tổng quỹ lương: " + currencyFormat.format(tong) + " VNĐ");
    }

    // ================= 2. VIEW DOANH THU THEO XE =================
    private JPanel createDoanhThuView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel filterCard = createWhiteCard();
        filterCard.setLayout(new BorderLayout(0, 12));

        JLabel lblHeader = new JLabel("Thống kê Doanh thu của mỗi Xe khách theo Khoảng ngày");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        filterCard.add(lblHeader, BorderLayout.NORTH);

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        formRow.setOpaque(false);

        LocalDate now = LocalDate.now();
        txtTuNgay = new JTextField(now.withDayOfYear(1).toString(), 10);
        txtDenNgay = new JTextField(now.toString(), 10);
        txtTuNgay.setPreferredSize(new Dimension(110, 32));
        txtDenNgay.setPreferredSize(new Dimension(110, 32));

        JButton btnFilter = createActionButton("Thống kê", PRIMARY_BLUE);
        btnFilter.addActionListener(e -> loadDoanhThu());

        JButton btnAllTime = createActionButton("Tất cả thời gian", new Color(100, 116, 139));
        btnAllTime.addActionListener(e -> {
            txtTuNgay.setText("2020-01-01");
            txtDenNgay.setText(LocalDate.now().plusYears(1).toString());
            loadDoanhThu();
        });

        lblTongDoanhThu = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTongDoanhThu.setForeground(EXCEL_GREEN);

        formRow.add(new JLabel("Từ ngày (YYYY-MM-DD):"));
        formRow.add(txtTuNgay);
        formRow.add(new JLabel("Đến ngày:"));
        formRow.add(txtDenNgay);
        formRow.add(btnFilter);
        formRow.add(btnAllTime);
        formRow.add(Box.createHorizontalStrut(15));
        formRow.add(lblTongDoanhThu);

        filterCard.add(formRow, BorderLayout.CENTER);

        JPanel tableCard = createWhiteCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Bảng Doanh thu theo Xe khách");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btnExcel = createActionButton("📊 Excel", EXCEL_GREEN);
        tableTop.add(lblTableTitle, BorderLayout.WEST);
        tableTop.add(btnExcel, BorderLayout.EAST);

        String[] cols = {"STT", "Biển số xe", "Hãng sản xuất", "Dòng xe (Model)", "Doanh thu (VNĐ)"};
        modelDoanhThu = createReadOnlyModel(cols);
        tblDoanhThu = new JTable(modelDoanhThu);
        styleTable(tblDoanhThu);

        btnExcel.addActionListener(e -> ExportReportUtil.exportJTableToCSV(tblDoanhThu, this));

        tableCard.add(tableTop, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(tblDoanhThu), BorderLayout.CENTER);

        panel.add(filterCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadDoanhThu() {
        try {
            Date d1 = Date.valueOf(txtTuNgay.getText().trim());
            Date d2 = Date.valueOf(txtDenNgay.getText().trim());

            if (d1.after(d2)) {
                JOptionPane.showMessageDialog(this,
                        "Ngày bắt đầu không được lớn hơn ngày kết thúc!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Object[]> data = thongKeDAO.getDoanhThuXe(d1, d2);
            modelDoanhThu.setRowCount(0);
            BigDecimal tong = BigDecimal.ZERO;
            int stt = 1;

            for (Object[] row : data) {
                BigDecimal dt = row[3] instanceof BigDecimal ? (BigDecimal) row[3] : BigDecimal.ZERO;
                tong = tong.add(dt);
                modelDoanhThu.addRow(new Object[]{
                        stt++,
                        row[0],
                        row[1],
                        row[2],
                        currencyFormat.format(dt)
                });
            }
            lblTongDoanhThu.setText("Tổng doanh thu: " + currencyFormat.format(tong) + " VNĐ");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng ngày không hợp lệ! Vui lòng nhập theo chuẩn YYYY-MM-DD (VD: 2026-01-01).",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= 3A. VIEW XE TRONG HẠN BẢO DƯỠNG =================
    private JPanel createXeTrongHanView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topCard = createWhiteCard();
        topCard.setLayout(new BorderLayout());

        JLabel lblHeader = new JLabel("Danh sách Xe khách Trong hạn Bảo dưỡng & Lịch Bảo dưỡng Tiếp theo");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblSoXeTrongHan = new JLabel("Số xe trong hạn: 0 xe");
        lblSoXeTrongHan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSoXeTrongHan.setForeground(EXCEL_GREEN);

        topCard.add(lblHeader, BorderLayout.WEST);
        topCard.add(lblSoXeTrongHan, BorderLayout.EAST);

        JPanel tableCard = createWhiteCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Lịch bảo dưỡng dự kiến (Chu kỳ 360 ngày trừ hao mòn quãng đường)");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);
        JButton btnRefresh = createActionButton("Làm mới", PRIMARY_BLUE);
        JButton btnExcel = createActionButton("📊 Excel", EXCEL_GREEN);
        rightActions.add(btnRefresh);
        rightActions.add(btnExcel);

        tableTop.add(lblTableTitle, BorderLayout.WEST);
        tableTop.add(rightActions, BorderLayout.EAST);

        String[] cols = {"STT", "Biển số", "Hãng sản xuất", "Model", "Ngày BD cuối", "Số ngày bị giảm", "Ngày BD tiếp theo"};
        modelTrongHan = createReadOnlyModel(cols);
        tblTrongHan = new JTable(modelTrongHan);
        styleTable(tblTrongHan);

        btnRefresh.addActionListener(e -> loadXeTrongHan());
        btnExcel.addActionListener(e -> ExportReportUtil.exportJTableToCSV(tblTrongHan, this));

        tableCard.add(tableTop, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(tblTrongHan), BorderLayout.CENTER);

        panel.add(topCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadXeTrongHan() {
        List<Object[]> data = thongKeDAO.getXeTrongHanBD();
        modelTrongHan.setRowCount(0);
        int stt = 1;
        for (Object[] row : data) {
            modelTrongHan.addRow(new Object[]{
                    stt++,
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4] + " ngày",
                    row[5]
            });
        }
        lblSoXeTrongHan.setText("Số xe trong hạn: " + data.size() + " xe");
    }

    // ================= 3B. VIEW XE QUÁ HẠN BẢO DƯỠNG =================
    private JPanel createXeQuaHanView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(CONTENT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topCard = createWhiteCard();
        topCard.setLayout(new BorderLayout());

        JLabel lblHeader = new JLabel("Cảnh báo Danh sách Xe khách Đã Quá hạn Bảo dưỡng");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(DANGER_RED);

        lblSoXeQuaHan = new JLabel("Số xe quá hạn: 0 xe");
        lblSoXeQuaHan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSoXeQuaHan.setForeground(DANGER_RED);

        topCard.add(lblHeader, BorderLayout.WEST);
        topCard.add(lblSoXeQuaHan, BorderLayout.EAST);

        JPanel tableCard = createWhiteCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Danh sách xe không đủ điều kiện phân công chuyến mới (RB04)");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);
        JButton btnRefresh = createActionButton("Làm mới", PRIMARY_BLUE);
        JButton btnExcel = createActionButton("📊 Excel", EXCEL_GREEN);
        rightActions.add(btnRefresh);
        rightActions.add(btnExcel);

        tableTop.add(lblTableTitle, BorderLayout.WEST);
        tableTop.add(rightActions, BorderLayout.EAST);

        String[] cols = {"STT", "Biển số", "Hãng sản xuất", "Model", "Ngày BD cuối", "Hạn BD tiếp theo (Đã quá hạn)"};
        modelQuaHan = createReadOnlyModel(cols);
        tblQuaHan = new JTable(modelQuaHan);
        styleTable(tblQuaHan);

        btnRefresh.addActionListener(e -> loadXeQuaHan());
        btnExcel.addActionListener(e -> ExportReportUtil.exportJTableToCSV(tblQuaHan, this));

        tableCard.add(tableTop, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(tblQuaHan), BorderLayout.CENTER);

        panel.add(topCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadXeQuaHan() {
        List<Object[]> data = thongKeDAO.getXeQuaHanBD();
        modelQuaHan.setRowCount(0);
        int stt = 1;
        for (Object[] row : data) {
            modelQuaHan.addRow(new Object[]{
                    stt++,
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4]
            });
        }
        lblSoXeQuaHan.setText("Số xe quá hạn: " + data.size() + " xe");
    }

    // ================= CÁC HÀM TIỆN ÍCH GIAO DIỆN =================
    private JPanel createWhiteCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));
        return card;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }

    private DefaultTableModel createReadOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }


    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setShowVerticalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer();

        headerRenderer.setBackground(SIDEBAR_BG);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setOpaque(true);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMaxWidth(60);
            table.getColumnModel().getColumn(0)
                    .setCellRenderer(centerRenderer);
        }
    }

}