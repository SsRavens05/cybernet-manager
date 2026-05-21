package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NhanVien {
    private final StringProperty maNV;
    private final StringProperty hoTen;
    private final StringProperty chucVu;
    private final StringProperty sdt;
    private final StringProperty luong; // Đã thêm cục này
    private final StringProperty caLam;
    private final StringProperty trangThai;
    private final StringProperty ngayVao;
    private final StringProperty maSoThue;
    private final StringProperty soBHYT;
    private final StringProperty ngayThoiViec;

    public NhanVien(String maNV, String hoTen, String chucVu, String sdt, String luong, String caLam, String trangThai, String ngayVao) {
        this(maNV, hoTen, chucVu, sdt, luong, caLam, trangThai, ngayVao, "—", "—", "—");
    }

    public NhanVien(String maNV, String hoTen, String maSoThue, String soBHYT, String ngayVao, String ngayThoiViec, String trangThai) {
        this(maNV, hoTen, "Nhân Viên", "—", "7.000.000đ", "Ca Sáng (6h-14h)", trangThai, ngayVao, maSoThue, soBHYT, ngayThoiViec);
    }

    public NhanVien(String maNV, String hoTen, String chucVu, String sdt, String luong, String caLam, String trangThai, String ngayVao, String maSoThue, String soBHYT, String ngayThoiViec) {
        this.maNV = new SimpleStringProperty(maNV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.chucVu = new SimpleStringProperty(chucVu);
        this.sdt = new SimpleStringProperty(sdt);
        this.luong = new SimpleStringProperty(luong);
        this.caLam = new SimpleStringProperty(caLam);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.ngayVao = new SimpleStringProperty(ngayVao);
        this.maSoThue = new SimpleStringProperty(maSoThue);
        this.soBHYT = new SimpleStringProperty(soBHYT);
        this.ngayThoiViec = new SimpleStringProperty(ngayThoiViec);
    }

    // --- PROPERTY ---
    public StringProperty maNVProperty() { return maNV; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty chucVuProperty() { return chucVu; }
    public StringProperty sdtProperty() { return sdt; }
    public StringProperty luongProperty() { return luong; }
    public StringProperty caLamProperty() { return caLam; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty ngayVaoProperty() { return ngayVao; }
    public StringProperty maSoThueProperty() { return maSoThue; }
    public StringProperty soBHYTProperty() { return soBHYT; }
    public StringProperty ngayThoiViecProperty() { return ngayThoiViec; }

    // --- GETTER ---
    public String getMaNV() { return maNV.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getChucVu() { return chucVu.get(); }
    public String getSdt() { return sdt.get(); }
    public String getLuong() { return luong.get(); }
    public String getCaLam() { return caLam.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getNgayVao() { return ngayVao.get(); }
    public String getMaSoThue() { return maSoThue.get(); }
    public String getSoBHYT() { return soBHYT.get(); }
    public String getNgayThoiViec() { return ngayThoiViec.get(); }

    // --- SETTER ---
    public void setMaNV(String value) { maNV.set(value); }
    public void setHoTen(String value) { hoTen.set(value); }
    public void setChucVu(String value) { chucVu.set(value); }
    public void setSdt(String value) { sdt.set(value); }
    public void setLuong(String value) { luong.set(value); }
    public void setCaLam(String value) { caLam.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setNgayVao(String value) { ngayVao.set(value); }
    public void setMaSoThue(String value) { maSoThue.set(value); }
    public void setSoBHYT(String value) { soBHYT.set(value); }
    public void setNgayThoiViec(String value) { ngayThoiViec.set(value); }
}