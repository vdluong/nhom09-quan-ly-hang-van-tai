

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