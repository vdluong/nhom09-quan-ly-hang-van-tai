

CREATE DATABASE IF NOT EXISTS quan_ly_van_tai_nhom09
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quan_ly_van_tai_nhom09;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS CHUYEN_XE;
DROP TABLE IF EXISTS CAU_HINH_GIOI_HAN;
DROP TABLE IF EXISTS TUYEN_XE;
DROP TABLE IF EXISTS TAI_XE;
DROP TABLE IF EXISTS XE_KHACH;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE XE_KHACH (
    bien_so VARCHAR(15) PRIMARY KEY,
    mau_xe VARCHAR(30) NOT NULL,
    hang_san_xuat VARCHAR(50) NOT NULL,
    doi_xe INT NOT NULL,
    model VARCHAR(50) NOT NULL,
    so_ghe INT NOT NULL CHECK (so_ghe >= 4),
    so_nam_su_dung INT NOT NULL CHECK (so_nam_su_dung >= 0),
    ngay_bao_duong_cuoi DATE NOT NULL
);

CREATE TABLE TAI_XE (
    ma_tai_xe VARCHAR(10) PRIMARY KEY,
    so_cmt VARCHAR(15) NOT NULL UNIQUE,
    ho_ten VARCHAR(100) NOT NULL,
    ma_bang_lai VARCHAR(20) NOT NULL UNIQUE,
    loai_bang_lai VARCHAR(10) NOT NULL,
    dia_chi VARCHAR(200) NOT NULL,
    ngay_sinh DATE NOT NULL,
    tham_nien INT NOT NULL CHECK (tham_nien >= 0),
    CONSTRAINT chk_tai_xe_loai_bang_lai CHECK (loai_bang_lai IN ('D', 'E', 'FC'))
);

CREATE TABLE TUYEN_XE (
    ma_tuyen VARCHAR(10) PRIMARY KEY,
    diem_dau VARCHAR(100) NOT NULL,
    diem_cuoi VARCHAR(100) NOT NULL,
    do_dai_km DECIMAL(8,2) NOT NULL CHECK (do_dai_km > 0),
    do_phuc_tap INT NOT NULL CHECK (do_phuc_tap IN (1, 2, 3)),
    CONSTRAINT chk_tuyen_dau_cuoi CHECK (diem_dau <> diem_cuoi)
);

CREATE TABLE CHUYEN_XE (
    ma_chuyen VARCHAR(15) PRIMARY KEY,
    bien_so VARCHAR(15) NOT NULL,
    ma_tuyen VARCHAR(10) NOT NULL,
    ma_lai_xe VARCHAR(10) NOT NULL,
    ma_phu_xe VARCHAR(10) NOT NULL,
    ngay_khoi_hanh DATETIME NOT NULL,
    so_khach INT NOT NULL CHECK (so_khach >= 0),
    gia_ve DECIMAL(12,2) NOT NULL CHECK (gia_ve > 0),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'Hoàn thành',
    CONSTRAINT fk_cx_xe FOREIGN KEY (bien_so) REFERENCES XE_KHACH(bien_so),
    CONSTRAINT fk_cx_tuyen FOREIGN KEY (ma_tuyen) REFERENCES TUYEN_XE(ma_tuyen),
    CONSTRAINT fk_cx_laixe FOREIGN KEY (ma_lai_xe) REFERENCES TAI_XE(ma_tai_xe),
    CONSTRAINT fk_cx_phuxe FOREIGN KEY (ma_phu_xe) REFERENCES TAI_XE(ma_tai_xe),
    CONSTRAINT chk_khac_taixe CHECK (ma_lai_xe <> ma_phu_xe),
    CONSTRAINT chk_cx_trang_thai CHECK (trang_thai IN ('Đã lên lịch', 'Đang chạy', 'Hoàn thành'))
);

-- Bảng cấu hình giới hạn số bản ghi (RB05).
CREATE TABLE CAU_HINH_GIOI_HAN (
    ten_bang VARCHAR(30) PRIMARY KEY,
    so_luong_toi_da INT NOT NULL CHECK (so_luong_toi_da > 0)
);

INSERT INTO CAU_HINH_GIOI_HAN (ten_bang, so_luong_toi_da) VALUES
('XE_KHACH', 100),
('TAI_XE', 200),
('TUYEN_XE', 100),
('CHUYEN_XE', 10000);

DELIMITER $$

-- RB01: Số khách không được vượt quá số ghế thiết kế trừ 2.
CREATE TRIGGER trg_chuyen_xe_rb01_bi
BEFORE INSERT ON CHUYEN_XE
FOR EACH ROW
BEGIN
    DECLARE v_so_ghe INT;
    SELECT so_ghe INTO v_so_ghe
    FROM XE_KHACH
    WHERE bien_so = NEW.bien_so;

    IF v_so_ghe IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'RB01: Bien so xe khach khong ton tai';
    END IF;

    IF NEW.so_khach > v_so_ghe - 2 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'RB01: so_khach khong duoc vuot qua so_ghe - 2';
    END IF;

    IF NEW.ma_lai_xe = NEW.ma_phu_xe THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Lai xe chinh va phu xe phai la hai tai xe khac nhau';
    END IF;
END$$

CREATE TRIGGER trg_chuyen_xe_rb01_bu
BEFORE UPDATE ON CHUYEN_XE
FOR EACH ROW
BEGIN
    DECLARE v_so_ghe INT;
    SELECT so_ghe INTO v_so_ghe
    FROM XE_KHACH
    WHERE bien_so = NEW.bien_so;

    IF v_so_ghe IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'RB01: Bien so xe khach khong ton tai';
    END IF;

    IF NEW.so_khach > v_so_ghe - 2 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'RB01: so_khach khong duoc vuot qua so_ghe - 2';
    END IF;

    IF NEW.ma_lai_xe = NEW.ma_phu_xe THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Lai xe chinh va phu xe phai la hai tai xe khac nhau';
    END IF;
END$$

-- RB05: Chặn thêm bản ghi khi đạt hạn mức cấu hình.
CREATE TRIGGER trg_xe_khach_rb05
BEFORE INSERT ON XE_KHACH
FOR EACH ROW
BEGIN
    DECLARE v_gioi_han INT;
    DECLARE v_so_ban_ghi INT;
    SELECT so_luong_toi_da INTO v_gioi_han
    FROM CAU_HINH_GIOI_HAN WHERE ten_bang = 'XE_KHACH';
    SELECT COUNT(*) INTO v_so_ban_ghi FROM XE_KHACH;
    IF v_gioi_han IS NOT NULL AND v_so_ban_ghi >= v_gioi_han THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'RB05: Da dat gioi han XE_KHACH';
    END IF;
END$$

CREATE TRIGGER trg_tai_xe_rb05
BEFORE INSERT ON TAI_XE
FOR EACH ROW
BEGIN
    DECLARE v_gioi_han INT;
    DECLARE v_so_ban_ghi INT;
    SELECT so_luong_toi_da INTO v_gioi_han
    FROM CAU_HINH_GIOI_HAN WHERE ten_bang = 'TAI_XE';
    SELECT COUNT(*) INTO v_so_ban_ghi FROM TAI_XE;
    IF v_gioi_han IS NOT NULL AND v_so_ban_ghi >= v_gioi_han THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'RB05: Da dat gioi han TAI_XE';
    END IF;
END$$

CREATE TRIGGER trg_tuyen_xe_rb05
BEFORE INSERT ON TUYEN_XE
FOR EACH ROW
BEGIN
    DECLARE v_gioi_han INT;
    DECLARE v_so_ban_ghi INT;
    SELECT so_luong_toi_da INTO v_gioi_han
    FROM CAU_HINH_GIOI_HAN WHERE ten_bang = 'TUYEN_XE';
    SELECT COUNT(*) INTO v_so_ban_ghi FROM TUYEN_XE;
    IF v_gioi_han IS NOT NULL AND v_so_ban_ghi >= v_gioi_han THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'RB05: Da dat gioi han TUYEN_XE';
    END IF;
END$$

CREATE TRIGGER trg_chuyen_xe_rb05
BEFORE INSERT ON CHUYEN_XE
FOR EACH ROW
BEGIN
    DECLARE v_gioi_han INT;
    DECLARE v_so_ban_ghi INT;
    SELECT so_luong_toi_da INTO v_gioi_han
    FROM CAU_HINH_GIOI_HAN WHERE ten_bang = 'CHUYEN_XE';
    SELECT COUNT(*) INTO v_so_ban_ghi FROM CHUYEN_XE;
    IF v_gioi_han IS NOT NULL AND v_so_ban_ghi >= v_gioi_han THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'RB05: Da dat gioi han CHUYEN_XE';
    END IF;
END$$

-- Kiểm tra tuổi tối thiểu 24 khi thêm/sửa tài xế.
CREATE TRIGGER trg_tai_xe_tuoi_bi
BEFORE INSERT ON TAI_XE
FOR EACH ROW
BEGIN
    IF NEW.ngay_sinh > DATE_SUB(CURDATE(), INTERVAL 24 YEAR) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tai xe phai du 24 tuoi';
    END IF;
END$$

CREATE TRIGGER trg_tai_xe_tuoi_bu
BEFORE UPDATE ON TAI_XE
FOR EACH ROW
BEGIN
    IF NEW.ngay_sinh > DATE_SUB(CURDATE(), INTERVAL 24 YEAR) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tai xe phai du 24 tuoi';
    END IF;
END$$

DELIMITER ;


-- ============================================================
-- 02_seed_data.sql
-- Chay sau 01_schema_and_constraints.sql
-- Du lieu mau: 10 xe, 12 tai xe, 8 tuyen, 30 chuyen
-- Co chuyen thang 10 va 11/2026; co xe qua han bao duong.
-- ============================================================
USE quan_ly_van_tai_nhom09;
SET NAMES utf8mb4;

INSERT INTO XE_KHACH
(bien_so, mau_xe, hang_san_xuat, doi_xe, model, so_ghe, so_nam_su_dung, ngay_bao_duong_cuoi)
VALUES
('29B-123.45','Trắng','Hyundai',2021,'Universe',45,5,'2025-08-01'),
('30F-234.56','Đỏ','Thaco',2022,'Mobihome',35,4,'2025-12-01'),
('29B-345.67','Xanh','Samco',2020,'Limousine',29,6,'2025-01-10'),
('30F-456.78','Bạc','Hyundai',2023,'Universe',45,3,'2026-03-01'),
('29B-567.89','Đen','Mercedes',2022,'Sprinter',16,4,'2026-01-15'),
('30F-678.90','Trắng','Thaco',2019,'Mobihome',35,7,'2024-10-01'),
('29B-789.01','Xám','Samco',2021,'Universe',45,5,'2025-02-15'),
('30F-890.12','Xanh','Hyundai',2024,'Limousine',29,2,'2026-06-01'),
('29B-901.23','Đỏ','Thaco',2020,'Mobihome',35,6,'2025-03-10'),
('30F-012.34','Bạc','Mercedes',2023,'Sprinter',16,3,'2026-02-01');

INSERT INTO TAI_XE
(ma_tai_xe, so_cmt, ho_ten, ma_bang_lai, loai_bang_lai, dia_chi, ngay_sinh, tham_nien)
VALUES
('TX001','001090000001','Nguyễn Văn An','GPLX001','E','Hà Nội','1988-04-12',12),
('TX002','001090000002','Trần Minh Bình','GPLX002','FC','Hà Nội','1985-07-20',15),
('TX003','001090000003','Lê Quốc Cường','GPLX003','D','Bắc Ninh','1990-03-05',9),
('TX004','001090000004','Phạm Đức Dũng','GPLX004','E','Hải Phòng','1987-11-17',13),
('TX005','001090000005','Hoàng Văn Giang','GPLX005','FC','Nam Định','1984-01-22',17),
('TX006','001090000006','Vũ Thành Hùng','GPLX006','D','Hà Nội','1992-09-09',8),
('TX007','001090000007','Đặng Tuấn Kiệt','GPLX007','E','Thanh Hóa','1989-05-30',11),
('TX008','001090000008','Bùi Anh Long','GPLX008','FC','Ninh Bình','1986-02-14',14),
('TX009','001090000009','Đỗ Quang Minh','GPLX009','D','Hà Nam','1991-12-03',8),
('TX010','001090000010','Ngô Hải Nam','GPLX010','E','Hà Nội','1983-08-25',18),
('TX011','001090000011','Đinh Xuân Phúc','GPLX011','FC','Thái Bình','1988-06-11',12),
('TX012','001090000012','Mai Trung Sơn','GPLX012','D','Hưng Yên','1993-10-19',7);

INSERT INTO TUYEN_XE (ma_tuyen, diem_dau, diem_cuoi, do_dai_km, do_phuc_tap) VALUES
('T01','Hà Nội','Hải Phòng',120.00,1),
('T02','Hà Nội','Nam Định',90.00,1),
('T03','Hà Nội','Thanh Hóa',160.00,2),
('T04','Hà Nội','Ninh Bình',95.00,1),
('T05','Hà Nội','Vinh',300.00,2),
('T06','Hà Nội','Sapa',320.00,3),
('T07','Hải Phòng','Móng Cái',180.00,3),
('T08','Hà Nội','Đà Nẵng', 760.00,3);

INSERT INTO CHUYEN_XE
(ma_chuyen, bien_so, ma_tuyen, ma_lai_xe, ma_phu_xe, ngay_khoi_hanh, so_khach, gia_ve, trang_thai)
VALUES
('CX20261001','29B-123.45','T01','TX001','TX002','2026-10-01 06:00:00',30,180000,'Hoàn thành'),
('CX20261002','30F-234.56','T02','TX003','TX004','2026-10-02 07:00:00',24,150000,'Hoàn thành'),
('CX20261003','29B-345.67','T03','TX005','TX006','2026-10-03 05:30:00',20,250000,'Hoàn thành'),
('CX20261004','30F-456.78','T04','TX007','TX008','2026-10-04 08:00:00',28,140000,'Hoàn thành'),
('CX20261005','29B-567.89','T05','TX009','TX010','2026-10-05 06:15:00',12,350000,'Hoàn thành'),
('CX20261006','30F-678.90','T06','TX011','TX012','2026-10-06 04:45:00',25,300000,'Hoàn thành'),
('CX20261007','29B-789.01','T07','TX002','TX003','2026-10-07 05:00:00',32,220000,'Hoàn thành'),
('CX20261008','30F-890.12','T08','TX004','TX005','2026-10-08 04:30:00',20,650000,'Hoàn thành'),
('CX20261009','29B-901.23','T01','TX006','TX007','2026-10-09 06:00:00',25,180000,'Hoàn thành'),
('CX20261010','30F-012.34','T02','TX008','TX009','2026-10-10 07:00:00',10,150000,'Hoàn thành'),
('CX20261011','29B-123.45','T03','TX010','TX011','2026-10-11 05:30:00',38,250000,'Hoàn thành'),
('CX20261012','30F-234.56','T04','TX012','TX001','2026-10-12 08:00:00',30,140000,'Hoàn thành'),
('CX20261013','29B-345.67','T05','TX003','TX005','2026-10-13 06:15:00',18,350000,'Hoàn thành'),
('CX20261014','30F-456.78','T06','TX007','TX009','2026-10-14 04:45:00',40,300000,'Đang chạy'),
('CX20261015','29B-567.89','T07','TX011','TX002','2026-10-15 05:00:00',14,220000,'Hoàn thành'),
('CX20261016','30F-678.90','T08','TX001','TX004','2026-10-16 04:30:00',30,650000,'Đang chạy'),
('CX20261017','29B-789.01','T01','TX005','TX008','2026-10-17 06:00:00',35,180000,'Đã lên lịch'),
('CX20261018','30F-890.12','T02','TX009','TX012','2026-10-18 07:00:00',22,150000,'Đã lên lịch'),
('CX20261019','29B-901.23','T03','TX002','TX006','2026-10-19 05:30:00',28,250000,'Đã lên lịch'),
('CX20261020','30F-012.34','T04','TX004','TX010','2026-10-20 08:00:00',12,140000,'Đã lên lịch'),
('CX20261101','29B-123.45','T05','TX006','TX011','2026-11-01 06:15:00',40,350000,'Đã lên lịch'),
('CX20261102','30F-234.56','T06','TX008','TX003','2026-11-02 04:45:00',30,300000,'Đã lên lịch'),
('CX20261103','29B-345.67','T07','TX010','TX007','2026-11-03 05:00:00',20,220000,'Đã lên lịch'),
('CX20261104','30F-456.78','T08','TX012','TX005','2026-11-04 04:30:00',35,650000,'Đã lên lịch'),
('CX20261105','29B-567.89','T01','TX001','TX009','2026-11-05 06:00:00',12,180000,'Đã lên lịch'),
('CX20261106','30F-678.90','T02','TX003','TX010','2026-11-06 07:00:00',26,150000,'Đã lên lịch'),
('CX20261107','29B-789.01','T03','TX005','TX012','2026-11-07 05:30:00',40,250000,'Đã lên lịch'),
('CX20261108','30F-890.12','T04','TX007','TX001','2026-11-08 08:00:00',18,140000,'Đã lên lịch'),
('CX20261109','29B-901.23','T05','TX009','TX004','2026-11-09 06:15:00',30,350000,'Đã lên lịch'),
('CX20261110','30F-012.34','T06','TX011','TX006','2026-11-10 04:45:00',10,300000,'Đã lên lịch');

-- Kiem tra nhanh so luong du lieu sau khi nap.
SELECT 'XE_KHACH' AS bang, COUNT(*) AS so_ban_ghi FROM XE_KHACH
UNION ALL SELECT 'TAI_XE', COUNT(*) FROM TAI_XE
UNION ALL SELECT 'TUYEN_XE', COUNT(*) FROM TUYEN_XE
UNION ALL SELECT 'CHUYEN_XE', COUNT(*) FROM CHUYEN_XE;


-- ============================================================
-- 03_queries_yeu_cau_2.sql
-- Cac truy van theo he so thong nhat trong phieu ban giao.
-- MySQL 8.0+
-- ============================================================
USE quan_ly_van_tai_nhom09;

-- TRUY VAN 1: Luong tai xe trong thang hien tai tinh den ngay hien tai.
SELECT
    tx.ma_tai_xe,
    tx.ho_ten,
    COALESCE(SUM(
        300000 *
        CASE vt.vai_tro WHEN 'LAI_CHINH' THEN 2.0 ELSE 1.0 END *
        CASE txe.do_phuc_tap WHEN 1 THEN 1.0 WHEN 2 THEN 1.5 WHEN 3 THEN 2.0 END
    ), 0) AS luong_thang_vnd
FROM TAI_XE tx
LEFT JOIN (
    SELECT ma_chuyen, ma_lai_xe AS ma_tai_xe, 'LAI_CHINH' AS vai_tro
    FROM CHUYEN_XE
    WHERE ngay_khoi_hanh >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
      AND ngay_khoi_hanh < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
      AND DATE(ngay_khoi_hanh) <= CURDATE()
    UNION ALL
    SELECT ma_chuyen, ma_phu_xe AS ma_tai_xe, 'PHU_XE' AS vai_tro
    FROM CHUYEN_XE
    WHERE ngay_khoi_hanh >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
      AND ngay_khoi_hanh < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
      AND DATE(ngay_khoi_hanh) <= CURDATE()
) vt ON vt.ma_tai_xe = tx.ma_tai_xe
LEFT JOIN CHUYEN_XE cx ON cx.ma_chuyen = vt.ma_chuyen
LEFT JOIN TUYEN_XE txe ON txe.ma_tuyen = cx.ma_tuyen
GROUP BY tx.ma_tai_xe, tx.ho_ten
ORDER BY luong_thang_vnd DESC, tx.ma_tai_xe;

-- TRUY VAN 2: Doanh thu cua moi xe trong khoang ngay.
SET @tu_ngay = '2026-10-01';
SET @den_ngay = '2026-10-31';

SELECT
    xk.bien_so,
    xk.hang_san_xuat,
    xk.model,
    COALESCE(SUM(cx.so_khach * cx.gia_ve), 0) AS doanh_thu_vnd
FROM XE_KHACH xk
LEFT JOIN CHUYEN_XE cx
    ON cx.bien_so = xk.bien_so
   AND DATE(cx.ngay_khoi_hanh) BETWEEN @tu_ngay AND @den_ngay
GROUP BY xk.bien_so, xk.hang_san_xuat, xk.model
ORDER BY doanh_thu_vnd DESC, xk.bien_so;

-- TRUY VAN 3A: Ngay bao duong tiep theo cua tat ca xe.
WITH km_quy_doi AS (
    SELECT
        cx.bien_so,
        SUM(tx.do_dai_km *
            CASE tx.do_phuc_tap
                WHEN 1 THEN 1.0
                WHEN 2 THEN 1.2
                WHEN 3 THEN 1.5
            END) AS tong_km
    FROM CHUYEN_XE cx
    JOIN TUYEN_XE tx ON tx.ma_tuyen = cx.ma_tuyen
    JOIN XE_KHACH xk ON xk.bien_so = cx.bien_so
    WHERE DATE(cx.ngay_khoi_hanh) > xk.ngay_bao_duong_cuoi
    GROUP BY cx.bien_so
)
SELECT
    xk.bien_so,
    xk.hang_san_xuat,
    xk.model,
    xk.ngay_bao_duong_cuoi,
    COALESCE(FLOOR(k.tong_km / 100), 0) AS so_ngay_bi_giam,
    DATE_ADD(
        xk.ngay_bao_duong_cuoi,
        INTERVAL (360 - COALESCE(FLOOR(k.tong_km / 100), 0)) DAY
    ) AS ngay_bao_duong_tiep
FROM XE_KHACH xk
LEFT JOIN km_quy_doi k ON k.bien_so = xk.bien_so
ORDER BY ngay_bao_duong_tiep, xk.bien_so;

-- TRUY VAN 3B: Danh sach rieng cac xe da qua han bao duong.
WITH km_quy_doi AS (
    SELECT
        cx.bien_so,
        SUM(tx.do_dai_km *
            CASE tx.do_phuc_tap
                WHEN 1 THEN 1.0
                WHEN 2 THEN 1.2
                WHEN 3 THEN 1.5
            END) AS tong_km
    FROM CHUYEN_XE cx
    JOIN TUYEN_XE tx ON tx.ma_tuyen = cx.ma_tuyen
    JOIN XE_KHACH xk ON xk.bien_so = cx.bien_so
    WHERE DATE(cx.ngay_khoi_hanh) > xk.ngay_bao_duong_cuoi
    GROUP BY cx.bien_so
),
lich_bao_duong AS (
    SELECT
        xk.bien_so,
        xk.hang_san_xuat,
        xk.model,
        xk.ngay_bao_duong_cuoi,
        DATE_ADD(
            xk.ngay_bao_duong_cuoi,
            INTERVAL (360 - COALESCE(FLOOR(k.tong_km / 100), 0)) DAY
        ) AS ngay_bao_duong_tiep
    FROM XE_KHACH xk
    LEFT JOIN km_quy_doi k ON k.bien_so = xk.bien_so
)
SELECT *
FROM lich_bao_duong
WHERE ngay_bao_duong_tiep < CURDATE()
ORDER BY ngay_bao_duong_tiep, bien_so;