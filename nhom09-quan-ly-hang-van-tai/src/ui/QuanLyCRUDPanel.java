package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;
import dao.XeKhachDAO;
import model.XeKhach;
import java.util.List;
import dao.TaiXeDAO;
import dao.TuyenXeDAO;
import dao.ChuyenXeDAO;
import model.TaiXe;
import model.TuyenXe;
import model.ChuyenXe;

public class QuanLyCRUDPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel contentCards;
    private Consumer<String> onNavigate;

    // Nút chuyển module trên Sidebar
    private JButton btnNavHome, btnNavXe, btnNavTaiXe, btnNavTuyen, btnNavChuyen;

    // === TAB XE KHÁCH ===
    public JTextField txtBienSo;
    public JTextField txtMauXe;
    public JTextField txtModel;
    public JComboBox<String> cbHangSX;
    public JSpinner spnDoiXe;
    public JSpinner spnNamSD;
    public JSpinner spnSoGhe;
    public JLabel lblSoChoToiDa;
    public JTextField txtNgayBaoDuongCuoi;
    public JTable tblXeKhach;
    public DefaultTableModel modelXeKhach;

    // === TAB TÀI XẾ ===
    public JTextField txtMaTaiXe;
    public JTextField txtSoCMT;
    public JTextField txtHoTen;
    public JTextField txtMaBangLai;
    public JComboBox<String> cbLoaiBangLai;
    public JTextField txtNgaySinh;
    public JTextField txtDiaChi;
    public JSpinner spnThamNien;
    public JTable tblTaiXe;
    public DefaultTableModel modelTaiXe;

    // === TAB TUYẾN XE ===
    public JTextField txtMaTuyen;
    public JTextField txtDiemDau;
    public JTextField txtDiemCuoi;
    public JTextField txtDoDaiKm;
    public JComboBox<String> cbDoPhucTap;
    public JTable tblTuyenXe;
    public DefaultTableModel modelTuyenXe;

    // === TAB CHUYẾN XE ===
    public JTextField txtMaChuyen;
    public JComboBox<String> cbBienSo;
    public JComboBox<String> cbMaTuyen;
    public JComboBox<String> cbMaLaiXe;
    public JComboBox<String> cbMaPhuXe;
    public JTextField txtNgayKhoiHanh;
    public JSpinner spnSoKhach;
    public JLabel lblGioiHanKhach;
    public JTextField txtGiaVe;
    public JComboBox<String> cbTrangThai;
    public JTable tblChuyenXe;
    public DefaultTableModel modelChuyenXe;

    private JLabel lblGioiHanXe = new JLabel();
    private JLabel lblGioiHanTaiXe = new JLabel();
    private JLabel lblGioiHanTuyen = new JLabel();
    private JLabel lblGioiHanChuyen = new JLabel();

    public QuanLyCRUDPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252)); // Slate-50 Nền xám nhạt toàn trang

        // === 1. SIDEBAR BÊN TRÁI (DARK NAVY SLATE) ===
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(15, 23, 42)); // #0F172A Dark Slate
        sidebar.setPreferredSize(new Dimension(210, 0));

        // Header Sidebar: Logo Bus + Brand Title
        JPanel sideHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 18));
        sideHeader.setOpaque(false);
        JLabel lblLogo = new JLabel("");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel lblTitle = new JLabel("<html><b>HỆ THỐNG QUẢN LÝ</b><br><small style='color:#94A3B8'>VẬN TẢI HÀNH KHÁCH</small></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.WHITE);
        sideHeader.add(lblLogo);
        sideHeader.add(lblTitle);

        // Menu items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(15, 10, 10, 10));

        btnNavHome = createSidebarButton("Trang chủ", false);
        btnNavXe = createSidebarButton(" Quản lý xe khách", true);
        btnNavTaiXe = createSidebarButton("Quản lý tài xế", false);
        btnNavTuyen = createSidebarButton("Quản lý tuyến xe", false);
        btnNavChuyen = createSidebarButton("Quản lý chuyến xe", false);

        // ĐIỀU HƯỚNG TRANG CHỦ BÊN THANH SIDEBAR BÊN TRÁI
        btnNavHome.addActionListener(e -> {
            if (this.onNavigate != null) {
                this.onNavigate.accept("DASHBOARD");
            }
        });

        btnNavXe.addActionListener(e -> selectSidebarItem("XE_KHACH", btnNavXe));
        btnNavTaiXe.addActionListener(e -> selectSidebarItem("TAI_XE", btnNavTaiXe));
        btnNavTuyen.addActionListener(e -> selectSidebarItem("TUYEN_XE", btnNavTuyen));
        btnNavChuyen.addActionListener(e -> selectSidebarItem("CHUYEN_XE", btnNavChuyen));

        menuPanel.add(btnNavHome);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnNavXe);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnNavTaiXe);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnNavTuyen);
        menuPanel.add(Box.createVerticalStrut(5));
        menuPanel.add(btnNavChuyen);

        // Footer Sidebar: Admin Profile
        JPanel sideFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        sideFooter.setOpaque(false);
        JLabel lblAdmin = new JLabel("👤 Admin");
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAdmin.setForeground(new Color(226, 232, 240));
        sideFooter.add(lblAdmin);

        sidebar.add(sideHeader, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(sideFooter, BorderLayout.SOUTH);

        // === 2. VÙNG NỘI DUNG CHÍNH (CARDLAYOUT) ===
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setOpaque(false);
        contentCards.setBorder(new EmptyBorder(15, 18, 15, 18));

        contentCards.add(createXeKhachView(), "XE_KHACH");
        contentCards.add(createTaiXeView(), "TAI_XE");
        contentCards.add(createTuyenXeView(), "TUYEN_XE");
        contentCards.add(createChuyenXeView(), "CHUYEN_XE");

        add(sidebar, BorderLayout.WEST);
        add(contentCards, BorderLayout.CENTER);
    }

    public QuanLyCRUDPanel() {
        this(null);
    }

    public JButton getBtnNavHome() {
        return btnNavHome;
    }

    public void showModule(String moduleKey) {
        switch (moduleKey) {
            case "XE_KHACH": selectSidebarItem("XE_KHACH", btnNavXe); break;
            case "TAI_XE": selectSidebarItem("TAI_XE", btnNavTaiXe); break;
            case "TUYEN_XE": selectSidebarItem("TUYEN_XE", btnNavTuyen); break;
            case "CHUYEN_XE": selectSidebarItem("CHUYEN_XE", btnNavChuyen); break;
        }
    }

    public void setTab(int index) {
        switch (index) {
            case 0: selectSidebarItem("XE_KHACH", btnNavXe); break;
            case 1: selectSidebarItem("TAI_XE", btnNavTaiXe); break;
            case 2: selectSidebarItem("TUYEN_XE", btnNavTuyen); break;
            case 3: selectSidebarItem("CHUYEN_XE", btnNavChuyen); break;
        }
    }

    private void selectSidebarItem(String cardKey, JButton selectedBtn) {
        if ("CHUYEN_XE".equals(cardKey) && cbBienSo != null) {
            loadComboChuyenXe();
        }
        cardLayout.show(contentCards, cardKey);
        resetSidebarStyles();
        selectedBtn.setBackground(new Color(30, 41, 59)); // Active background
        selectedBtn.setForeground(Color.WHITE);
        selectedBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedBtn.repaint();
    }

    private void resetSidebarStyles() {
        JButton[] btns = {btnNavHome, btnNavXe, btnNavTaiXe, btnNavTuyen, btnNavChuyen};
        for (JButton b : btns) {
            b.setBackground(new Color(15, 23, 42));
            b.setForeground(new Color(148, 163, 184));
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            b.repaint();
        }
    }

    private JButton createSidebarButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setMaximumSize(new Dimension(190, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        if (active) {
            btn.setBackground(new Color(30, 41, 59));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else {
            btn.setBackground(new Color(15, 23, 42));
            btn.setForeground(new Color(148, 163, 184));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        return btn;
    }

    private JPanel createRoundedCardPanel() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(226, 232, 240));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 18, 16, 18));
        return p;
    }

    private JPanel createActionButtonGroup(Runnable onThem, Runnable onSua,
                                           Runnable onXoa, Runnable onLamMoi) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        JButton btnThem = makeButton("✓ Lưu mới", new Color(22, 163, 74));
        JButton btnSua = makeButton("✏ Cập nhật", new Color(2, 132, 199));
        JButton btnXoa = makeButton("🗑 Xóa", new Color(220, 38, 38));
        JButton btnLamMoi = makeButton("🔄 Làm mới", new Color(100, 116, 139));

        btnThem.addActionListener(e -> onThem.run());
        btnSua.addActionListener(e -> onSua.run());
        btnXoa.addActionListener(e -> onXoa.run());
        btnLamMoi.addActionListener(e -> onLamMoi.run());

        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        return panel;
    }

    private JButton makeButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        return b;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // === TẠO RENDERER RIÊNG ĐỂ ÉP MÀU NỀN TÍM THAN CHO HEADER ===
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(15, 23, 42)); // Màu tím than #0F172A
                label.setForeground(Color.WHITE);            // Chữ màu trắng
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(30, 41, 59))); // Đường viền mảnh giữa các cột
                return label;
            }
        };

        // Gán Custom Renderer vào từng cột của Header
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.getTableHeader().setReorderingAllowed(false);

        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        // Màu khi chọn dòng dữ liệu bên dưới
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(241, 245, 249));

        // Căn giữa dữ liệu các dòng trong bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    private JPanel createXeKhachView() {
        JPanel view = new JPanel(new BorderLayout(0, 12));
        view.setOpaque(false);

        JPanel formCard = createRoundedCardPanel();
        JLabel lblTitle = new JLabel("Thông tin Xe khách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel fieldsGrid = new JPanel(new GridLayout(3, 3, 15, 10));
        fieldsGrid.setOpaque(false);

        txtBienSo = new JTextField("VD: 29B-123.45");
        txtMauXe = new JTextField("VD: Trắng");
        txtModel = new JTextField("VD: Universe");
        cbHangSX = new JComboBox<>(new String[]{"Hyundai", "Thaco", "Samco", "Mercedes"});
        spnDoiXe = new JSpinner(new SpinnerNumberModel(2022, 1990, 2030, 1));
        spnNamSD = new JSpinner(new SpinnerNumberModel(2, 0, 50, 1));
        spnSoGhe = new JSpinner(new SpinnerNumberModel(29, 4, 100, 1));
        lblSoChoToiDa = new JLabel("58 KHÁCH");
        lblSoChoToiDa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtNgayBaoDuongCuoi = new JTextField("2026-01-01");

        spnSoGhe.addChangeListener(e -> {
            int soGhe = (int) spnSoGhe.getValue();
            lblSoChoToiDa.setText(Math.max(0, soGhe - 2) + " KHÁCH");
        });
        lblSoChoToiDa.setText(Math.max(0, (int) spnSoGhe.getValue() - 2) + " KHÁCH");

        fieldsGrid.add(createFieldGroup("Biển số xe:", txtBienSo));
        fieldsGrid.add(createFieldGroup("Màu sơn xe:", txtMauXe));
        fieldsGrid.add(createFieldGroup("Số khách tối đa:", lblSoChoToiDa));

        fieldsGrid.add(createFieldGroup("Dòng xe (Model):", txtModel));
        fieldsGrid.add(createFieldGroup("Hãng sản xuất:", cbHangSX));
        fieldsGrid.add(createFieldGroup("Số năm sử dụng:", spnNamSD));

        fieldsGrid.add(createFieldGroup("Đời xe (Năm SX):", spnDoiXe));
        fieldsGrid.add(createFieldGroup("Số ghế (>=4):", spnSoGhe));
        fieldsGrid.add(createFieldGroup("Ngày bảo dưỡng cuối:", txtNgayBaoDuongCuoi));

        JPanel topContent = new JPanel(new BorderLayout(0, 12));
        topContent.setOpaque(false);
        topContent.add(lblTitle, BorderLayout.NORTH);
        topContent.add(fieldsGrid, BorderLayout.CENTER);
        topContent.add(createActionButtonGroup(
                        this::themXeKhach, this::suaXeKhach, this::xoaXeKhach, this::lamMoiXeKhach),
                BorderLayout.SOUTH);

        formCard.add(topContent, BorderLayout.CENTER);

        JPanel tableCard = createRoundedCardPanel();

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Danh sách Xe khách");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel rightSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSearch.setOpaque(false);
        JTextField txtSearch = new JTextField("Tìm kiếm", 12);
        JButton btnFilter = new JButton("🔍");
        // Dòng ~300: Nút Excel
        JButton btnExcel = new JButton("📊 Excel");
        btnExcel.setBackground(new Color(22, 163, 74));
        btnExcel.setForeground(Color.WHITE);
        btnExcel.setOpaque(true);              // <-- THÊM DÒNG NÀY
        btnExcel.setContentAreaFilled(true);   // <-- THÊM DÒNG NÀY
        btnExcel.setBorderPainted(false);      // <-- THÊM DÒNG NÀY
        rightSearch.add(lblGioiHanXe);
        rightSearch.add(txtSearch);
        rightSearch.add(btnFilter);
        rightSearch.add(btnExcel);

        tableHeaderPanel.add(lblTableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(rightSearch, BorderLayout.EAST);

        String[] columns = {"Biển số", "Màu xe", "Model", "Hãng SX", "Đời xe", "Năm SD", "Số ghế", "Ngày BD cuối"};
        modelXeKhach = new DefaultTableModel(columns, 0);
        tblXeKhach = new JTable(modelXeKhach);
        styleTable(tblXeKhach);
        btnExcel.addActionListener(e -> utils.ExportReportUtil.exportJTableToCSV(tblXeKhach, this));
        tblXeKhach.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tblXeKhach.getSelectedRow();
            if (r < 0) return;
            txtBienSo.setText(String.valueOf(modelXeKhach.getValueAt(r, 0)));
            txtMauXe.setText(String.valueOf(modelXeKhach.getValueAt(r, 1)));
            txtModel.setText(String.valueOf(modelXeKhach.getValueAt(r, 2)));
            cbHangSX.setSelectedItem(modelXeKhach.getValueAt(r, 3));
            spnDoiXe.setValue(modelXeKhach.getValueAt(r, 4));
            spnNamSD.setValue(modelXeKhach.getValueAt(r, 5));
            spnSoGhe.setValue(modelXeKhach.getValueAt(r, 6));
            txtNgayBaoDuongCuoi.setText(String.valueOf(modelXeKhach.getValueAt(r, 7)));
            txtBienSo.setEditable(false);
        });



        JPanel tableContent = new JPanel(new BorderLayout(0, 10));
        tableContent.setOpaque(false);
        tableContent.add(tableHeaderPanel, BorderLayout.NORTH);
        tableContent.add(new JScrollPane(tblXeKhach), BorderLayout.CENTER);

        tableCard.add(tableContent, BorderLayout.CENTER);

        view.add(formCard, BorderLayout.NORTH);
        view.add(tableCard, BorderLayout.CENTER);

        return view;
    }

    private JPanel createTaiXeView() {
        JPanel view = new JPanel(new BorderLayout(0, 12));
        view.setOpaque(false);

        JPanel formCard = createRoundedCardPanel();
        JPanel fieldsGrid = new JPanel(new GridLayout(3, 3, 15, 10));
        fieldsGrid.setOpaque(false);

        txtMaTaiXe = new JTextField();
        txtSoCMT = new JTextField();
        txtHoTen = new JTextField();
        txtMaBangLai = new JTextField();
        cbLoaiBangLai = new JComboBox<>(new String[]{"D", "E", "FC"});
        txtNgaySinh = new JTextField("1990-01-01");
        txtDiaChi = new JTextField();
        spnThamNien = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));

        fieldsGrid.add(createFieldGroup("Mã tài xế:", txtMaTaiXe));
        fieldsGrid.add(createFieldGroup("Số CCCD/CMT:", txtSoCMT));
        fieldsGrid.add(createFieldGroup("Họ tên đầy đủ:", txtHoTen));
        fieldsGrid.add(createFieldGroup("Mã bằng lái:", txtMaBangLai));
        fieldsGrid.add(createFieldGroup("Loại bằng lái:", cbLoaiBangLai));
        fieldsGrid.add(createFieldGroup("Ngày sinh:", txtNgaySinh));
        fieldsGrid.add(createFieldGroup("Địa chỉ:", txtDiaChi));
        fieldsGrid.add(createFieldGroup("Thâm niên (năm):", spnThamNien));

        JPanel topContent = new JPanel(new BorderLayout(0, 12));
        topContent.setOpaque(false);
        topContent.add(new JLabel("Thông tin Tài xế"), BorderLayout.NORTH);
        topContent.add(fieldsGrid, BorderLayout.CENTER);
        topContent.add(createActionButtonGroup(
                        this::themTaiXe, this::suaTaiXe, this::xoaTaiXe, this::lamMoiTaiXe),
                BorderLayout.SOUTH);

        formCard.add(topContent, BorderLayout.CENTER);

        JPanel tableCard = createRoundedCardPanel();
        String[] columns = {"Mã TX", "Số CCCD", "Họ tên", "Mã bằng", "Loại bằng", "Ngày sinh", "Địa chỉ", "Thâm niên"};
        modelTaiXe = new DefaultTableModel(columns, 0);
        tblTaiXe = new JTable(modelTaiXe);
        styleTable(tblTaiXe);
        tblTaiXe.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tblTaiXe.getSelectedRow();
            if (r < 0) return;
            txtMaTaiXe.setText(String.valueOf(modelTaiXe.getValueAt(r, 0)));
            txtSoCMT.setText(String.valueOf(modelTaiXe.getValueAt(r, 1)));
            txtHoTen.setText(String.valueOf(modelTaiXe.getValueAt(r, 2)));
            txtMaBangLai.setText(String.valueOf(modelTaiXe.getValueAt(r, 3)));
            cbLoaiBangLai.setSelectedItem(modelTaiXe.getValueAt(r, 4));
            txtNgaySinh.setText(String.valueOf(modelTaiXe.getValueAt(r, 5)));
            txtDiaChi.setText(String.valueOf(modelTaiXe.getValueAt(r, 6)));
            spnThamNien.setValue(modelTaiXe.getValueAt(r, 7));
            txtMaTaiXe.setEditable(false);
        });


        JPanel tableContent = new JPanel(new BorderLayout(0, 10));
        tableContent.setOpaque(false);
        tableContent.add(new JLabel("Danh sách Tài xế"), BorderLayout.NORTH);
        tableContent.add(new JScrollPane(tblTaiXe), BorderLayout.CENTER);

        tableCard.add(tableContent, BorderLayout.CENTER);

        view.add(formCard, BorderLayout.NORTH);
        view.add(tableCard, BorderLayout.CENTER);
        loadTaiXe();
        return view;
    }

    private JPanel createTuyenXeView() {
        JPanel view = new JPanel(new BorderLayout(0, 12));
        view.setOpaque(false);

        JPanel formCard = createRoundedCardPanel();
        JPanel fieldsGrid = new JPanel(new GridLayout(2, 3, 15, 10));
        fieldsGrid.setOpaque(false);

        txtMaTuyen = new JTextField();
        txtDiemDau = new JTextField();
        txtDiemCuoi = new JTextField();
        txtDoDaiKm = new JTextField();
        cbDoPhucTap = new JComboBox<>(new String[]{"1 Cấp 1 (Dễ)", "2 Cấp 2 (TB)", "3 Cấp 3 (Khó)"});

        fieldsGrid.add(createFieldGroup("Mã tuyến:", txtMaTuyen));
        fieldsGrid.add(createFieldGroup("Điểm đầu:", txtDiemDau));
        fieldsGrid.add(createFieldGroup("Điểm cuối:", txtDiemCuoi));
        fieldsGrid.add(createFieldGroup("Độ dài (km):", txtDoDaiKm));
        fieldsGrid.add(createFieldGroup("Độ phức tạp:", cbDoPhucTap));

        JPanel topContent = new JPanel(new BorderLayout(0, 12));
        topContent.setOpaque(false);
        topContent.add(new JLabel("Thông tin Tuyến xe"), BorderLayout.NORTH);
        topContent.add(fieldsGrid, BorderLayout.CENTER);
        topContent.add(createActionButtonGroup(
                        this::themTuyenXe, this::suaTuyenXe, this::xoaTuyenXe, this::lamMoiTuyenXe),
                BorderLayout.SOUTH);
        formCard.add(topContent, BorderLayout.CENTER);

        JPanel tableCard = createRoundedCardPanel();
        String[] columns = {"Mã tuyến", "Điểm đầu", "Điểm cuối", "Độ dài (km)", "Độ phức tạp"};
        modelTuyenXe = new DefaultTableModel(columns, 0);
        tblTuyenXe = new JTable(modelTuyenXe);
        styleTable(tblTuyenXe);
        tblTuyenXe.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tblTuyenXe.getSelectedRow();
            if (r < 0) return;
            txtMaTuyen.setText(String.valueOf(modelTuyenXe.getValueAt(r, 0)));
            txtDiemDau.setText(String.valueOf(modelTuyenXe.getValueAt(r, 1)));
            txtDiemCuoi.setText(String.valueOf(modelTuyenXe.getValueAt(r, 2)));
            txtDoDaiKm.setText(String.valueOf(modelTuyenXe.getValueAt(r, 3)));
            cbDoPhucTap.setSelectedItem(modelTuyenXe.getValueAt(r, 4));
            txtMaTuyen.setEditable(false);
        });

        JPanel tableContent = new JPanel(new BorderLayout(0, 10));
        tableContent.setOpaque(false);
        tableContent.add(new JLabel("Danh sách Tuyến xe"), BorderLayout.NORTH);
        tableContent.add(new JScrollPane(tblTuyenXe), BorderLayout.CENTER);

        tableCard.add(tableContent, BorderLayout.CENTER);

        view.add(formCard, BorderLayout.NORTH);
        view.add(tableCard, BorderLayout.CENTER);
        loadTuyenXe();
        return view;
    }

    private JPanel createChuyenXeView() {
        JPanel view = new JPanel(new BorderLayout(0, 12));
        view.setOpaque(false);

        JPanel formCard = createRoundedCardPanel();
        JPanel fieldsGrid = new JPanel(new GridLayout(3, 3, 15, 10));
        fieldsGrid.setOpaque(false);

        txtMaChuyen = new JTextField();
        cbBienSo = new JComboBox<>(new String[]{"29B-123.45", "30F-987.65"});
        cbMaTuyen = new JComboBox<>(new String[]{"T01", "T02"});
        cbMaLaiXe = new JComboBox<>(new String[]{"TX001 - Nguyễn Văn A"});
        cbMaPhuXe = new JComboBox<>(new String[]{"TX002 - Trần Văn B"});
        txtNgayKhoiHanh = new JTextField("2026-10-25 08:00:00");
        spnSoKhach = new JSpinner(new SpinnerNumberModel(20, 0, 200, 1));
        lblGioiHanKhach = new JLabel("soGhe * 2 khách");
        txtGiaVe = new JTextField("150000");
        cbTrangThai = new JComboBox<>(new String[]{"Đã lên lịch", "Đang chạy", "Hoàn thành"});

        fieldsGrid.add(createFieldGroup("Mã chuyến:", txtMaChuyen));
        fieldsGrid.add(createFieldGroup("Biển số xe:", cbBienSo));
        fieldsGrid.add(createFieldGroup("Mã tuyến:", cbMaTuyen));
        fieldsGrid.add(createFieldGroup("Lái chính:", cbMaLaiXe));
        fieldsGrid.add(createFieldGroup("Phụ xe:", cbMaPhuXe));
        fieldsGrid.add(createFieldGroup("Khởi hành:", txtNgayKhoiHanh));
        fieldsGrid.add(createFieldGroup("Giá vé:", txtGiaVe));
        fieldsGrid.add(createFieldGroup("Trạng thái:", cbTrangThai));

        JPanel topContent = new JPanel(new BorderLayout(0, 12));
        topContent.setOpaque(false);
        topContent.add(new JLabel("Thông tin Chuyến xe"), BorderLayout.NORTH);
        topContent.add(fieldsGrid, BorderLayout.CENTER);
        topContent.add(createActionButtonGroup(
                        this::themChuyenXe, this::suaChuyenXe, this::xoaChuyenXe, this::lamMoiChuyenXe),
                BorderLayout.SOUTH);
        formCard.add(topContent, BorderLayout.CENTER);

        JPanel tableCard = createRoundedCardPanel();
        String[] columns = {"Mã chuyến", "Biển số", "Mã tuyến", "Lái chính", "Phụ xe", "Khởi hành", "Số khách", "Giá vé", "Trạng thái"};
        modelChuyenXe = new DefaultTableModel(columns, 0);
        tblChuyenXe = new JTable(modelChuyenXe);
        styleTable(tblChuyenXe);

        tblChuyenXe.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tblChuyenXe.getSelectedRow();
            if (r < 0) return;
            txtMaChuyen.setText(String.valueOf(modelChuyenXe.getValueAt(r, 0)));
            cbBienSo.setSelectedItem(modelChuyenXe.getValueAt(r, 1));
            cbMaTuyen.setSelectedItem(modelChuyenXe.getValueAt(r, 2));
            chonTheoMa(cbMaLaiXe, modelChuyenXe.getValueAt(r, 3));
            chonTheoMa(cbMaPhuXe, modelChuyenXe.getValueAt(r, 4));
            txtNgayKhoiHanh.setText(String.valueOf(modelChuyenXe.getValueAt(r, 5)));
            spnSoKhach.setValue(modelChuyenXe.getValueAt(r, 6));
            txtGiaVe.setText(String.valueOf(modelChuyenXe.getValueAt(r, 7)));
            cbTrangThai.setSelectedItem(modelChuyenXe.getValueAt(r, 8));
            txtMaChuyen.setEditable(false);
        });

        JPanel tableContent = new JPanel(new BorderLayout(0, 10));
        tableContent.setOpaque(false);
        tableContent.add(new JLabel("Danh sách Chuyến xe"), BorderLayout.NORTH);
        tableContent.add(new JScrollPane(tblChuyenXe), BorderLayout.CENTER);

        tableCard.add(tableContent, BorderLayout.CENTER);

        view.add(formCard, BorderLayout.NORTH);
        view.add(tableCard, BorderLayout.CENTER);
        loadComboChuyenXe();
        loadChuyenXe();
        return view;
    }

    private final XeKhachDAO xeKhachDAO = new XeKhachDAO();

    private void capNhatGioiHan() {
        lblGioiHanXe.setText("Số bản ghi: " + config.DBConnection.layThongTinGioiHan("XE_KHACH"));
        lblGioiHanTaiXe.setText("Số bản ghi: " + config.DBConnection.layThongTinGioiHan("TAI_XE"));
        lblGioiHanTuyen.setText("Số bản ghi: " + config.DBConnection.layThongTinGioiHan("TUYEN_XE"));
        lblGioiHanChuyen.setText("Số bản ghi: " + config.DBConnection.layThongTinGioiHan("CHUYEN_XE"));
    }

    private void loadXeKhach() {
        modelXeKhach.setRowCount(0);
        List<XeKhach> list = xeKhachDAO.getAll();
        for (XeKhach xk : list) {
            modelXeKhach.addRow(new Object[]{
                    xk.getBienSo(), xk.getMauXe(), xk.getModel(), xk.getHangSanXuat(),
                    xk.getDoiXe(), xk.getSoNamSuDung(), xk.getSoGhe(),
                    xk.getNgayBaoDuongCuoi()});
        }
    }

    private void lamMoiXeKhach() {
        txtBienSo.setText("");
        txtMauXe.setText("");
        txtModel.setText("");
        cbHangSX.setSelectedIndex(0);
        spnDoiXe.setValue(2022);
        spnNamSD.setValue(0);
        spnSoGhe.setValue(29);
        txtNgayBaoDuongCuoi.setText("2026-01-01");
        txtBienSo.setEditable(true);
        tblXeKhach.clearSelection();
    }

    /** Đọc form thành đối tượng XeKhach; trả về null nếu dữ liệu sai. */
    private XeKhach docFormXeKhach() {
        String bienSo = txtBienSo.getText().trim();
        if (bienSo.isEmpty() || bienSo.startsWith("VD:")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số xe!");
            return null;
        }
        java.sql.Date ngayBD;
        try {
            ngayBD = java.sql.Date.valueOf(txtNgayBaoDuongCuoi.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Ngày bảo dưỡng phải có dạng yyyy-MM-dd, ví dụ 2026-01-15");
            return null;
        }
        String mau = txtMauXe.getText().trim();
        String model = txtModel.getText().trim();
        if (mau.startsWith("VD:")) mau = "";
        if (model.startsWith("VD:")) model = "";

        return new XeKhach(bienSo, mau, (String) cbHangSX.getSelectedItem(),
                (int) spnDoiXe.getValue(), model, (int) spnSoGhe.getValue(),
                (int) spnNamSD.getValue(), ngayBD);
    }

    private void themXeKhach() {
        XeKhach xk = docFormXeKhach();
        if (xk == null) return;
        if (xeKhachDAO.insert(xk)) {
            JOptionPane.showMessageDialog(this, "Đã thêm xe khách!");
            loadXeKhach();
            lamMoiXeKhach();
        }
    }

    private void suaXeKhach() {
        if (tblXeKhach.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        XeKhach xk = docFormXeKhach();
        if (xk == null) return;
        if (xeKhachDAO.update(xk)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật!");
            loadXeKhach();
            lamMoiXeKhach();
        }
    }

    private void xoaXeKhach() {
        int r = tblXeKhach.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        String bienSo = String.valueOf(modelXeKhach.getValueAt(r, 0));
        if (JOptionPane.showConfirmDialog(this, "Xóa xe " + bienSo + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (xeKhachDAO.delete(bienSo)) {
                loadXeKhach();
                lamMoiXeKhach();
            }
        }
    }

    // ===================== TÀI XẾ =====================
    private final TaiXeDAO taiXeDAO = new TaiXeDAO();

    private void loadTaiXe() {
        modelTaiXe.setRowCount(0);
        for (TaiXe t : taiXeDAO.getAll()) {
            modelTaiXe.addRow(new Object[]{
                    t.getMaTaiXe(), t.getSoCmt(), t.getHoTen(), t.getMaBangLai(),
                    t.getLoaiBangLai(), t.getNgaySinh(), t.getDiaChi(), t.getThamNien()});
        }
    }

    private void lamMoiTaiXe() {
        txtMaTaiXe.setText("");
        txtSoCMT.setText("");
        txtHoTen.setText("");
        txtMaBangLai.setText("");
        cbLoaiBangLai.setSelectedIndex(0);
        txtNgaySinh.setText("1990-01-01");
        txtDiaChi.setText("");
        spnThamNien.setValue(1);
        txtMaTaiXe.setEditable(true);
        tblTaiXe.clearSelection();
    }

    private TaiXe docFormTaiXe() {
        String ma = txtMaTaiXe.getText().trim();
        String ten = txtHoTen.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã tài xế và Họ tên!");
            return null;
        }
        java.sql.Date ngaySinh;
        try {
            ngaySinh = java.sql.Date.valueOf(txtNgaySinh.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải có dạng yyyy-MM-dd, ví dụ 1990-05-20");
            return null;
        }
        return new TaiXe(ma, txtSoCMT.getText().trim(), ten, txtMaBangLai.getText().trim(),
                (String) cbLoaiBangLai.getSelectedItem(), txtDiaChi.getText().trim(),
                ngaySinh, (int) spnThamNien.getValue());
    }

    private void themTaiXe() {
        TaiXe t = docFormTaiXe();
        if (t == null) return;
        if (taiXeDAO.insert(t)) {
            JOptionPane.showMessageDialog(this, "Đã thêm tài xế!");
            loadTaiXe();
            lamMoiTaiXe();
        }
    }

    private void suaTaiXe() {
        if (tblTaiXe.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        TaiXe t = docFormTaiXe();
        if (t == null) return;
        if (taiXeDAO.update(t)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật!");
            loadTaiXe();
            lamMoiTaiXe();
        }
    }

    private void xoaTaiXe() {
        int r = tblTaiXe.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        String ma = String.valueOf(modelTaiXe.getValueAt(r, 0));
        if (JOptionPane.showConfirmDialog(this, "Xóa tài xế " + ma + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (taiXeDAO.delete(ma)) {
                loadTaiXe();
                lamMoiTaiXe();
            }
        }
    }

    // ===================== TUYẾN XE =====================
    private final TuyenXeDAO tuyenXeDAO = new TuyenXeDAO();

    private String nhanDoPhucTap(int cap) {
        return cbDoPhucTap.getItemAt(Math.max(0, Math.min(2, cap - 1)));
    }

    private void loadTuyenXe() {
        modelTuyenXe.setRowCount(0);
        for (TuyenXe t : tuyenXeDAO.getAll()) {
            modelTuyenXe.addRow(new Object[]{
                    t.getMaTuyen(), t.getDiemDau(), t.getDiemCuoi(),
                    t.getDoDaiKm(), nhanDoPhucTap(t.getDoPhucTap())});
        }
    }

    private void lamMoiTuyenXe() {
        txtMaTuyen.setText("");
        txtDiemDau.setText("");
        txtDiemCuoi.setText("");
        txtDoDaiKm.setText("");
        cbDoPhucTap.setSelectedIndex(0);
        txtMaTuyen.setEditable(true);
        tblTuyenXe.clearSelection();
    }

    private TuyenXe docFormTuyenXe() {
        String ma = txtMaTuyen.getText().trim();
        if (ma.isEmpty() || txtDiemDau.getText().trim().isEmpty()
                || txtDiemCuoi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã tuyến, Điểm đầu và Điểm cuối!");
            return null;
        }
        java.math.BigDecimal doDai;
        try {
            doDai = new java.math.BigDecimal(txtDoDaiKm.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Độ dài (km) phải là số, ví dụ 105.5");
            return null;
        }
        return new TuyenXe(ma, txtDiemDau.getText().trim(), txtDiemCuoi.getText().trim(),
                doDai, cbDoPhucTap.getSelectedIndex() + 1);
    }

    private void themTuyenXe() {
        TuyenXe t = docFormTuyenXe();
        if (t == null) return;
        if (tuyenXeDAO.insert(t)) {
            JOptionPane.showMessageDialog(this, "Đã thêm tuyến xe!");
            loadTuyenXe();
            lamMoiTuyenXe();
        }
    }

    private void suaTuyenXe() {
        if (tblTuyenXe.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        TuyenXe t = docFormTuyenXe();
        if (t == null) return;
        if (tuyenXeDAO.update(t)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật!");
            loadTuyenXe();
            lamMoiTuyenXe();
        }
    }

    private void xoaTuyenXe() {
        int r = tblTuyenXe.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        String ma = String.valueOf(modelTuyenXe.getValueAt(r, 0));
        if (JOptionPane.showConfirmDialog(this, "Xóa tuyến " + ma + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (tuyenXeDAO.delete(ma)) {
                loadTuyenXe();
                lamMoiTuyenXe();
            }
        }
    }

    // ===================== CHUYẾN XE =====================
    private final ChuyenXeDAO chuyenXeDAO = new ChuyenXeDAO();

    /** Nạp lại 4 ô chọn từ database (biển số, tuyến, lái chính, phụ xe). */
    private void loadComboChuyenXe() {
        cbBienSo.removeAllItems();
        for (model.XeKhach x : xeKhachDAO.getAll()) cbBienSo.addItem(x.getBienSo());

        cbMaTuyen.removeAllItems();
        for (TuyenXe t : tuyenXeDAO.getAll()) cbMaTuyen.addItem(t.getMaTuyen());

        cbMaLaiXe.removeAllItems();
        cbMaPhuXe.removeAllItems();
        for (TaiXe t : taiXeDAO.getAll()) {
            cbMaLaiXe.addItem(t.toString());   // dạng "TX001 - Nguyễn Văn A"
            cbMaPhuXe.addItem(t.toString());
        }
    }

    /** Lấy mã từ chuỗi "TX001 - Nguyễn Văn A". */
    private String maTuLabel(Object item) {
        return item == null ? null : item.toString().split(" - ")[0].trim();
    }

    private void chonTheoMa(JComboBox<String> cb, Object ma) {
        if (ma == null) return;
        String m = ma.toString();
        for (int i = 0; i < cb.getItemCount(); i++) {
            String it = cb.getItemAt(i);
            if (it.equals(m) || it.startsWith(m + " - ")) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    private String dinhDangNgayGio(java.sql.Timestamp ts) {
        if (ts == null) return "";
        String s = ts.toString();
        return s.length() > 19 ? s.substring(0, 19) : s;
    }

    private void loadChuyenXe() {
        modelChuyenXe.setRowCount(0);
        for (ChuyenXe c : chuyenXeDAO.getAll()) {
            modelChuyenXe.addRow(new Object[]{
                    c.getMaChuyen(), c.getBienSo(), c.getMaTuyen(), c.getMaLaiXe(), c.getMaPhuXe(),
                    dinhDangNgayGio(c.getNgayKhoiHanh()), c.getSoKhach(),
                    c.getGiaVe() == null ? "" : c.getGiaVe().toPlainString(),
                    c.getTrangThai()});
        }
    }

    private void lamMoiChuyenXe() {
        txtMaChuyen.setText("");
        if (cbBienSo.getItemCount() > 0) cbBienSo.setSelectedIndex(0);
        if (cbMaTuyen.getItemCount() > 0) cbMaTuyen.setSelectedIndex(0);
        if (cbMaLaiXe.getItemCount() > 0) cbMaLaiXe.setSelectedIndex(0);
        if (cbMaPhuXe.getItemCount() > 0) cbMaPhuXe.setSelectedIndex(0);
        txtNgayKhoiHanh.setText("2026-10-25 08:00:00");
        spnSoKhach.setValue(20);
        txtGiaVe.setText("150000");
        cbTrangThai.setSelectedIndex(0);
        txtMaChuyen.setEditable(true);
        tblChuyenXe.clearSelection();
    }

    private ChuyenXe docFormChuyenXe() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã chuyến!");
            return null;
        }
        if (cbBienSo.getSelectedItem() == null || cbMaTuyen.getSelectedItem() == null
                || cbMaLaiXe.getSelectedItem() == null || cbMaPhuXe.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Cần có đủ Xe, Tuyến, Lái chính và Phụ xe. Hãy thêm dữ liệu ở các tab kia trước!");
            return null;
        }
        java.sql.Timestamp ngay;
        try {
            ngay = java.sql.Timestamp.valueOf(txtNgayKhoiHanh.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Khởi hành phải có dạng yyyy-MM-dd HH:mm:ss, ví dụ 2026-10-25 08:00:00");
            return null;
        }
        java.math.BigDecimal giaVe;
        try {
            giaVe = new java.math.BigDecimal(txtGiaVe.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá vé phải là số, ví dụ 150000");
            return null;
        }
        return new ChuyenXe(ma, (String) cbBienSo.getSelectedItem(),
                (String) cbMaTuyen.getSelectedItem(),
                maTuLabel(cbMaLaiXe.getSelectedItem()), maTuLabel(cbMaPhuXe.getSelectedItem()),
                ngay, (int) spnSoKhach.getValue(), giaVe,
                (String) cbTrangThai.getSelectedItem());
    }

    private void themChuyenXe() {
        ChuyenXe c = docFormChuyenXe();
        if (c == null) return;
        if (chuyenXeDAO.insert(c)) {
            JOptionPane.showMessageDialog(this, "Đã thêm chuyến xe!");
            loadChuyenXe();
            lamMoiChuyenXe();
        }
    }

    private void suaChuyenXe() {
        if (tblChuyenXe.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        ChuyenXe c = docFormChuyenXe();
        if (c == null) return;
        if (chuyenXeDAO.update(c)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật!");
            loadChuyenXe();
            lamMoiChuyenXe();
        }
    }

    private void xoaChuyenXe() {
        int r = tblChuyenXe.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một dòng trong bảng trước!");
            return;
        }
        String ma = String.valueOf(modelChuyenXe.getValueAt(r, 0));
        if (JOptionPane.showConfirmDialog(this, "Xóa chuyến " + ma + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (chuyenXeDAO.delete(ma)) {
                loadChuyenXe();
                lamMoiChuyenXe();
            }
        }
    }

    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(71, 85, 105));
        p.add(lbl, BorderLayout.NORTH);
        p.add(component, BorderLayout.CENTER);
        return p;
    }
}
