<div align="center">🚍 HỆ THỐNG QUẢN LÝ HÃNG VẬN TẢI HÀNH KHÁCH ĐƯỜNG DÀI
Bài tập lớn Cơ sở dữ liệu — Nhóm 09
HỌC VIỆN CÔNG NGHỆ BƯU CHÍNH VIỄN THÔNG (PTIT)

Java Swing · MySQL · JDBC
</div>📖 Giới thiệu
Dự án xây dựng ứng dụng desktop hỗ trợ quản lý hoạt động của một hãng vận tải hành khách đường dài. Hệ thống quản lý xe khách, tài xế, tuyến xe, chuyến xe và cung cấp các truy vấn phục vụ thống kê lương, doanh thu và lịch bảo dưỡng.

Dự án được thực hiện trong khuôn khổ bài tập lớn môn Cơ sở dữ liệu, sử dụng Java Swing cho giao diện và MySQL để lưu trữ dữ liệu.
✨ Chức năng chính
- Quản lý xe khách: thêm, xem, sửa, xóa và theo dõi thông tin phương tiện.
- Quản lý tài xế: lưu thông tin cá nhân, bằng lái và thâm niên.
- Quản lý tuyến xe: quản lý điểm đầu, điểm cuối, quãng đường và độ phức tạp.
- Quản lý chuyến xe: phân công xe, lái xe chính, phụ xe, ngày khởi hành, số khách, giá vé và trạng thái.
- Thống kê lương tài xế: tổng hợp tiền công theo vai trò và các chuyến đáp ứng điều kiện tính lương.
- Thống kê doanh thu xe: tổng hợp doanh thu theo khoảng thời gian.
- Theo dõi bảo dưỡng: tính ngày bảo dưỡng tiếp theo dựa trên ngày bảo dưỡng gần nhất và quãng đường quy đổi.
- Giao diện quản trị: dashboard, các màn hình quản lý và chức năng xuất dữ liệu/báo cáo.
  Các ràng buộc dữ liệu nổi bật
- RB01: Số hành khách trên chuyến không vượt quá so_ghe - 2.
- RB04: Kiểm tra hạn bảo dưỡng trước khi bố trí xe vào chuyến.
- RB05: Kiểm soát số lượng bản ghi tối đa theo cấu hình.
- Kiểm tra lái xe chính và phụ xe khác nhau, tuổi tài xế và các điều kiện hợp lệ của dữ liệu.
  🛠️ Công nghệ sử dụng
  Thành phần	Công nghệ
  Ngôn ngữ	Java (JDK 17 trở lên)
  Giao diện	Java Swing
  Cơ sở dữ liệu	MySQL 8.0+
  Kết nối CSDL	JDBC / MySQL Connector/J 8.3.0
  IDE	IntelliJ IDEA
  Thiết kế CSDL	Mô hình EER, lược đồ quan hệ


🗂️ Cấu trúc dự án
nhom09-quan-ly-hang-van-tai/
├── database/
│   ├── 01_schema_and_constraints.sql
│   ├── 02_seed_data.sql
│   ├── 03_queries_yeu_cau_2.sql
│   └── nhom09_full_submission.sql
├── docs/
│   └── Bao_Cao_Yeu_Cau_1_EER.pdf
├── lib/
│   └── mysql-connector-j-8.3.0.jar
├── src/
│   ├── config/
│   ├── dao/
│   ├── model/
│   ├── ui/
│   └── utils/
├── tests/
│   └── kich_ban_kiem_thu_testcases.md
├── .gitignore
└── README.md
Nếu repository trên GitHub đang chứa thêm một thư mục nhom09-quan-ly-hang-van-tai/ ở bên trong, các đường dẫn trên được tính từ thư mục dự án chứa src/ và database/.

🗄️ Thiết kế cơ sở dữ liệu
CSDL sử dụng tên quan_ly_van_tai_nhom09, gồm 5 bảng chính:
Bảng	Vai trò
XE_KHACH	Lưu thông tin xe khách và ngày bảo dưỡng cuối
TAI_XE	Lưu thông tin tài xế và bằng lái
TUYEN_XE	Lưu thông tin tuyến đường
CHUYEN_XE	Lưu các chuyến xe và thông tin phân công
CAU_HINH_GIOI_HAN	Cấu hình giới hạn số bản ghi


🚀 Hướng dẫn cài đặt và chạy
1. Yêu cầu môi trường
- JDK 17 hoặc mới hơn.
- MySQL Server 8.0 hoặc mới hơn.
- MySQL Workbench (để chạy các script SQL).
- IntelliJ IDEA.
2. Tải mã nguồn
   git clone https://github.com/vdluong/nhom09-quan-ly-hang-van-tai.git
   Mở thư mục chứa src/, database/ và lib/ trong IntelliJ IDEA.
3. Khởi tạo CSDL
   Mở MySQL Workbench, kết nối tới MySQL Server và thực hiện theo thứ tự:
1. Chạy database/01_schema_and_constraints.sql để tạo bảng, khóa và ràng buộc.
2. Chạy database/02_seed_data.sql để nạp dữ liệu mẫu.
3. Có thể chạy database/03_queries_yeu_cau_2.sql để kiểm tra các truy vấn yêu cầu.
   ⚠️ Chú ý: Script 01_schema_and_constraints.sql có các lệnh DROP TABLE. Hãy sao lưu dữ liệu trước khi chạy lại trên CSDL đang sử dụng.

4. Cấu hình kết nối MySQL
   Ứng dụng đọc thông tin kết nối từ JVM properties hoặc biến môi trường, không cần đưa mật khẩu thật vào mã nguồn.

Trong IntelliJ IDEA, mở Run → Edit Configurations → VM options và nhập:
-Ddb.user=root
-Ddb.password=MAT_KHAU_MYSQL_CUA_BAN
URL mặc định:
jdbc:mysql://localhost:3306/quan_ly_van_tai_nhom09?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
Nếu cần, có thể tùy chỉnh bằng -Ddb.url=... hoặc các biến môi trường DB_URL, DB_USER, DB_PASSWORD.

Không commit mật khẩu MySQL lên GitHub.
5. Chạy ứng dụng
1. Mở dự án trong IntelliJ IDEA.
2. Thêm file lib/mysql-connector-j-8.3.0.jar vào Project Structure → Modules → Dependencies nếu IDE chưa nhận thư viện.
3. Mở src/ui/MainFrame.java.
4. Chọn Run để khởi động ứng dụng.
   Nếu kết nối thất bại, hãy kiểm tra MySQL Server, tên CSDL, cổng 3306, tài khoản và mật khẩu.

Các tình huống đã được thao tác kiểm tra trong quá trình phát triển gồm: từ chối xếp xe quá hạn bảo dưỡng (RB04), kiểm tra rollback dữ liệu, kiểm tra tính lương tài xế theo trạng thái chuyến và tính doanh thu xe. Kết quả kiểm thử chính thức cần được ghi lại theo từng test case trước khi nghiệm thu.
👥 Thành viên Nhóm 09
STT	Họ và tên	Mã sinh viên
1	Vũ Đức Lương	B24DCCN375
2	Nguyễn Xuân Bách	B24DCCN055
3	Lê Xuân Sơn	B24DCCN496
4	Phạm Phúc Phong	B24DCCN441
5	Lê Việt Anh	B24DCCN024


Đơn vị: Học viện Công nghệ Bưu chính Viễn thông (PTIT) — Hà Nội.
📄 Tài liệu
- [Script tạo CSDL và ràng buộc](database/01_schema_and_constraints.sql)
- [Dữ liệu mẫu](database/02_seed_data.sql)
- [Các truy vấn yêu cầu](database/03_queries_yeu_cau_2.sql)
<div align="center">NHÓM 09 · PTIT · BÀI TẬP LỚN CƠ SỞ DỮ LIỆU
</div>
