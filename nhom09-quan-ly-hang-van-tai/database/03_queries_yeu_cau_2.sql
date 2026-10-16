-- ============================================================
-- 03_queries_yeu_cau_2.sql
-- Cac truy van theo he so thong nhat trong phieu ban giao.
-- MySQL 8.0+
-- ============================================================
USE quan_ly_van_tai_nhom09;

-- TRUY VAN 1: Luong tai xe trong thang hien tai tinh den ngay hien tai.
-- Don gia goc: 300.000/chuyen; lai chinh x2; phu xe x1.
-- He so tuyen: cap 1 x1; cap 2 x1.5; cap 3 x2.
-- LEFT JOIN giup hien ca tai xe chua co chuyen, luong = 0.
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
-- Thay hai gia tri ngay ben duoi bang ngay can thong ke.
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
-- Tong km quy doi chi tinh chuyen sau ngay bao duong cuoi.
-- He so duong: cap 1 x1; cap 2 x1.2; cap 3 x1.5.
-- Moi 100 km quy doi lam giam 1 ngay trong chu ky 360 ngay.
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
