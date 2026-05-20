package com.example.cybergame_management;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

final class DatabaseSeedData {
    private DatabaseSeedData() {
    }

    static ObservableList<KhachHang> khachHang() {
        return FXCollections.observableArrayList(
                // KHACHHANG: MAKH, HOTEN, SODU, SODIEMTICHLUY, TRANGTHAI, CREATE_AT
                new KhachHang("KH001", "Nguyen Van An", "250000", "1250", "ACTIVE", "2026-05-01"),
                new KhachHang("KH002", "Tran Thi Binh", "82000", "820", "ACTIVE", "2026-05-03"),
                new KhachHang("KH003", "Le Minh Cuong", "0", "0", "INACTIVE", "2026-05-08")
        );
    }

    static ObservableList<NhanVien> nhanVien() {
        return FXCollections.observableArrayList(
                // NHAN_VIEN joined with APP_USER, LOAI_NHAN_VIEN and CHI_TIET_CA_LAM
                new NhanVien("NV001", "Nguyen Thanh Long", "Quan ly ca", "0901111222", "8000000", "CA_SANG", "DANG_LAM", "2025-01-15"),
                new NhanVien("NV002", "Tran Minh Khoa", "Thu ngan", "0912222333", "5500000", "CA_CHIEU", "DANG_LAM", "2025-03-20"),
                new NhanVien("NV003", "Le Thi Hoa", "Ky thuat", "0923333444", "6500000", "CA_TOI", "NGHI_PHEP", "2025-06-01"),
                new NhanVien("NV004", "Pham Van Tu", "Bao tri PC", "0934444555", "6000000", "CA_SANG", "DANG_LAM", "2025-09-10")
        );
    }

    static ObservableList<KhuVuc> khuVuc() {
        return FXCollections.observableArrayList(
                // KHUVUC joined with LOAIKHUVUC: MAKV, TENKV, SOMAYKV, GIA, TRANGTHAI, TENLOAIKV
                new KhuVuc("KV001", "Khu VIP", "10", "15000", "HOATDONG", "LKV_VIP"),
                new KhuVuc("KV002", "Khu Thuong", "20", "10000", "HOATDONG", "LKV_THUONG"),
                new KhuVuc("KV003", "Khu Esport", "8", "20000", "HOATDONG", "LKV_ESPORT"),
                new KhuVuc("KV004", "Khu Offline", "5", "8000", "BAOTRI", "LKV_OFFLINE")
        );
    }

    static ObservableList<SanPham> sanPham() {
        return FXCollections.observableArrayList(
                // SAN_PHAM: MASP, TENSP, LOAISP, DONGIABQ, SOLUONGTK, DVT
                new SanPham("SP001", "Mi Hao Hao", "Do an", "5000", "120", "Goi", "CON_HANG"),
                new SanPham("SP002", "Pepsi Lon", "Do uong", "12000", "60", "Lon", "CON_HANG"),
                new SanPham("SP003", "Snack Oishi", "Do an", "10000", "0", "Goi", "HET_HANG"),
                new SanPham("SP004", "Tra sua", "Do uong", "25000", "30", "Ly", "CON_HANG"),
                new SanPham("SP005", "Banh mi", "Do an", "15000", "20", "Cai", "CON_HANG")
        );
    }

    static ObservableList<NhapHang> nhapHang() {
        return FXCollections.observableArrayList(
                // NHAP_HANG joined with CHI_TIET_NHAP_HANG, NHA_CUNG_CAP and SAN_PHAM
                new NhapHang("NH001", "Pepsi Lon", "NCC001 - Suntory PepsiCo", "50", "12000", "600000", "2026-05-06", "NV001", "DA_NHAP"),
                new NhapHang("NH002", "Mi Hao Hao", "NCC002 - Acecook", "100", "5000", "500000", "2026-05-06", "NV001", "DA_NHAP"),
                new NhapHang("NH003", "Tai nghe Gaming", "NCC003 - Logitech", "10", "800000", "8000000", "2026-05-07", "NV002", "CHO_DUYET")
        );
    }

    static ObservableList<ThietBi> thietBi() {
        return FXCollections.observableArrayList(
                // THIET_BI: MATB, TENTB, LOAITB, TRANGTHAI, MAPC
                new ThietBi("TB001", "Chuot Logitech G102", "Chuot", "HOATDONG", "PC001"),
                new ThietBi("TB002", "Ban phim co DareU", "Ban phim", "HOATDONG", "PC001"),
                new ThietBi("TB003", "Man hinh Samsung 27 inch", "Man hinh", "BAOTRI", "PC002"),
                new ThietBi("TB004", "Tai nghe DareU EH722X", "Tai nghe", "HONG", "PC003")
        );
    }

    static ObservableList<KhuyenMai> khuyenMai() {
        return FXCollections.observableArrayList(
                // CHUONG_TRINH_KHUYEN_MAI: MACTR, TENCTR, LOAICTR, CHIETKHAU, NGBD, NGKT
                new KhuyenMai("CTR001", "Giam gia cuoi tuan", "GIAM_GIA", "20%", "SAN_PHAM", "2026-05-01", "2026-05-31", "DANG_AP_DUNG"),
                new KhuyenMai("CTR002", "Khuyen mai do uong", "GIAM_GIA", "10%", "SAN_PHAM", "2026-05-01", "2026-06-30", "DANG_AP_DUNG"),
                new KhuyenMai("CTR003", "Tet giam gia", "GIAM_GIA", "30%", "SAN_PHAM", "2026-02-01", "2026-02-10", "HET_HAN")
        );
    }

    static ObservableList<SuKien> caLam() {
        return FXCollections.observableArrayList(
                // Screen reused for CA_LAM because the database has no SU_KIEN table.
                new SuKien("CA_SANG", "Ca sang", "2026-05-20", "08:00", "12:00", "4", "0", "DANG_DIEN_RA"),
                new SuKien("CA_CHIEU", "Ca chieu", "2026-05-20", "13:00", "17:00", "3", "0", "SAP_DIEN_RA"),
                new SuKien("CA_TOI", "Ca toi", "2026-05-20", "18:00", "22:00", "3", "0", "SAP_DIEN_RA")
        );
    }

    static ObservableList<QuanLyTaiChinhController.GiaoDich> giaoDich() {
        return FXCollections.observableArrayList(
                // PHIEU_THANH_TOAN and PHIEU_NHAP_KHO represented in one finance ledger.
                new QuanLyTaiChinhController.GiaoDich("PTT001", "2026-05-06 09:15:00", "KH001 - Nguyen Van An", "THANH_TOAN", 200000, "Thanh toan gio choi va dich vu"),
                new QuanLyTaiChinhController.GiaoDich("PTT002", "2026-05-06 10:30:00", "KH002 - Tran Thi Binh", "THANH_TOAN", 50000, "Thanh toan gio choi"),
                new QuanLyTaiChinhController.GiaoDich("PNK001", "2026-05-06 14:00:00", "NV001 - Nguyen Thanh Long", "NHAP_KHO", -1100000, "Nhap san pham kho"),
                new QuanLyTaiChinhController.GiaoDich("NH001", "2026-05-07 10:00:00", "NCC003 - Logitech", "NHAP_HANG", -8000000, "Nhap thiet bi")
        );
    }
}
