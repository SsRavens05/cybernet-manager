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
                new NhanVien("NV001", "Nguyễn Thành Long", "Quản Lý", "0901111222", "12.000.000đ", "Ca Sáng (6h-14h)", "Đang làm", "2024-01-15", "8201234567", "DN4020123456789", "—"),
                new NhanVien("NV002", "Trần Minh Khoa", "Kỹ Thuật", "0912222333", "8.000.000đ", "Ca Chiều (14h-22h)", "Đang làm", "2024-03-20", "8209876543", "DN4020987654321", "—"),
                new NhanVien("NV003", "Lê Thị Hoa", "Nhân Viên", "0923333444", "7.000.000đ", "Ca Đêm (22h-6h)", "Nghỉ phép", "2024-06-01", "8201122334", "DN4021122334455", "—"),
                new NhanVien("NV004", "Phạm Văn Tú", "Bảo Vệ", "0934444555", "6.000.000đ", "Ca Sáng (6h-14h)", "Đang làm", "2024-09-10", "8205544332", "DN402554432211", "—"),
                new NhanVien("NV005", "Hoàng Thị Mai", "Phục Vụ", "0945555666", "6.500.000đ", "Ca Chiều (14h-22h)", "Nghỉ việc", "2023-11-05", "8206677889", "DN402667788900", "2025-11-05")
        );
    }

    static ObservableList<LoaiNhanVien> loaiNhanVien() {
        return FXCollections.observableArrayList(
                new LoaiNhanVien("LNV001", "Quản Lý", "12.000.000 đ"),
                new LoaiNhanVien("LNV002", "Kỹ Thuật", "8.000.000 đ"),
                new LoaiNhanVien("LNV003", "Nhân Viên", "7.000.000 đ"),
                new LoaiNhanVien("LNV004", "Bảo Vệ", "6.000.000 đ"),
                new LoaiNhanVien("LNV005", "Phục Vụ", "6.500.000 đ")
        );
    }

    static ObservableList<CaLam> caLamShifts() {
        return FXCollections.observableArrayList(
                new CaLam("CA001", "06:00", "12:00", "6h", "Đã kết thúc", "—"),
                new CaLam("CA002", "12:00", "18:00", "6h", "Đang làm", "—"),
                new CaLam("CA003", "18:00", "24:00", "6h", "Sắp tới", "+1h"),
                new CaLam("CA004", "06:00", "12:00", "6h", "Sắp tới", "—"),
                new CaLam("CA005", "00:00", "06:00", "6h", "Đã kết thúc", "+2h"),
                new CaLam("CA006", "08:00", "20:00", "12h", "Đã kết thúc", "—")
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

    static ObservableList<LoaiKhuVuc> loaiKhuVuc() {
        return FXCollections.observableArrayList(
                new LoaiKhuVuc("LKV001", "VIP", "10", "15000", "2025-01-01 08:00:00"),
                new LoaiKhuVuc("LKV002", "Thường", "20", "10000", "2025-01-01 08:00:00"),
                new LoaiKhuVuc("LKV003", "Esport", "8", "20000", "2025-01-01 08:00:00"),
                new LoaiKhuVuc("LKV004", "Offline", "5", "8000", "2025-03-15 10:30:00")
        );
    }

    static ObservableList<SanPham> sanPham() {
        return FXCollections.observableArrayList(
                // SAN_PHAM: MASP, TENSP, LOAISP, DONGIABQ, SOLUONGTK, DVT, SODIEMTICHLUY
                new SanPham("SP001", "Mi Hao Hao", "Do an", "5000", "120", "Goi", "5"),
                new SanPham("SP002", "Pepsi Lon", "Do uong", "12000", "60", "Lon", "12"),
                new SanPham("SP003", "Snack Oishi", "Do an", "10000", "0", "Goi", "10"),
                new SanPham("SP004", "Tra sua", "Do uong", "25000", "30", "Ly", "25"),
                new SanPham("SP005", "Banh mi", "Do an", "15000", "20", "Cai", "15")
        );
    }

    static ObservableList<NhapHang> nhapHang() {
        return FXCollections.observableArrayList(
                // NHAP_HANG: MAPN, LOAIHANG, NHACC, SOLUONG, DONGIA, TONGTIENNHAP, NGAYNHAP, NGUOINHAP, TRANGTHAI
                new NhapHang("PN001", "Nước uống", "NCC001 - Suntory PepsiCo", "50", "12.000đ", "600.000đ", "2026-05-06", "NV001", "DA_NHAP"),
                new NhapHang("PN002", "Đồ ăn", "NCC002 - Acecook", "100", "5.000đ", "500.000đ", "2026-05-06", "NV001", "DA_NHAP"),
                new NhapHang("PN003", "Thiết bị", "NCC003 - Logitech", "10", "800.000đ", "8.000.000đ", "2026-05-07", "NV002", "CHO_DUYET")
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
                new KhuyenMai("CTR001", "Giảm 20% cuối tuần", "GIAM_GIA", "20%", "2025-05-01", "2025-05-31", "DANG_AP_DUNG"),
                new KhuyenMai("CTR002", "Nạp 200k tặng 50k", "TANG_QUA", "50.000đ", "2025-05-01", "2025-06-30", "DANG_AP_DUNG"),
                new KhuyenMai("CTR003", "Tặng 1h chơi sinh nhật", "TANG_GIO", "1h", "2025-01-01", "2025-12-31", "DANG_AP_DUNG"),
                new KhuyenMai("CTR004", "Tết Giảm 30%", "GIAM_GIA", "30%", "2025-01-25", "2025-02-05", "HET_HAN")
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

    private static final ObservableList<DichVuDaDung> dichVuDaDungData = FXCollections.observableArrayList(
            new DichVuDaDung("DVSD20250508090714", "SP001", "1", "Serviced", "2025-05-08 09:07:14.0"),
            new DichVuDaDung("DVSD20250508090714", "SP002", "1", "Serviced", "2025-05-08 09:07:14.0"),
            new DichVuDaDung("DVSD20250508090714", "SP003", "1", "Serviced", "2025-05-08 09:07:14.0"),
            new DichVuDaDung("DVSD20250508091208", "SP001", "1", "Serviced", "2025-05-08 09:12:08.0"),
            new DichVuDaDung("DVSD20250504162043", "SP002", "1", "Serviced", "2025-05-04 16:20:43.0"),
            new DichVuDaDung("DVSD20250504162510", "SP001", "2", "Serviced", "2025-05-04 16:25:10.0")
    );

    static ObservableList<DichVuDaDung> dichVuDaDung() {
        return dichVuDaDungData;
    }
}
