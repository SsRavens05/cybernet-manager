package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SanPham {
    private final StringProperty maSP;
    private final StringProperty tenSP;
    private final StringProperty loai;
    private final StringProperty gia;
    private final StringProperty soLuong;
    private final StringProperty donVi;
    private final StringProperty trangThai;

    public SanPham(String maSP, String tenSP, String loai, String gia, String soLuong, String donVi, String trangThai) {
        this.maSP = new SimpleStringProperty(maSP);
        this.tenSP = new SimpleStringProperty(tenSP);
        this.loai = new SimpleStringProperty(loai);
        this.gia = new SimpleStringProperty(gia);
        this.soLuong = new SimpleStringProperty(soLuong);
        this.donVi = new SimpleStringProperty(donVi);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    public StringProperty maSPProperty() { return maSP; }
    public StringProperty tenSPProperty() { return tenSP; }
    public StringProperty loaiProperty() { return loai; }
    public StringProperty giaProperty() { return gia; }
    public StringProperty soLuongProperty() { return soLuong; }
    public StringProperty donViProperty() { return donVi; }
    public StringProperty trangThaiProperty() { return trangThai; }
}