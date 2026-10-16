# Nhóm 09 – Quản lý hàng vận tải

Dự án Java Swing + MySQL phục vụ bài tập lớn Cơ sở dữ liệu.

## Công nghệ
- Java (Swing/JDBC)
- MySQL
- MySQL Connector/J

## Cấu trúc
- `docs/`: báo cáo và sơ đồ ERD
- `database/`: schema, dữ liệu mẫu và truy vấn
- `lib/`: thư viện JDBC
- `src/config/`: cấu hình kết nối CSDL
- `src/model/`: các lớp mô hình
- `src/dao/`: truy vấn dữ liệu
- `src/ui/`: giao diện Swing
- `src/utils/`: kiểm tra dữ liệu và xuất báo cáo
- `tests/`: tài liệu kiểm thử

## Chạy dự án
1. Cài JDK và MySQL.
2. Tạo database bằng cách chạy `database/01_schema_and_constraints.sql`.
3. Nạp dữ liệu mẫu bằng `database/02_seed_data.sql` (nếu có).
4. Cập nhật thông tin kết nối trong `src/config/DBConnection.java`.
5. Thêm MySQL Connector/J vào classpath của IDE.
6. Chạy `src/ui/MainFrame.java`.

> Các tệp hiện là bộ khung ban đầu; cần bổ sung nội dung nghiệp vụ, SQL và giao diện theo yêu cầu bài tập.
