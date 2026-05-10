package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NhapHang {
    private final StringProperty maNH;
    private final StringProperty tenSP;
    private final StringProperty nhaCC;
    private final StringProperty soLuong;
    private final StringProperty donGia;
    private final StringProperty tongTien;
    private final StringProperty ngayNhap;
    private final StringProperty nguoiNhap;
    private final StringProperty trangThai;

    public NhapHang(String maNH, String tenSP, String nhaCC, String soLuong, String donGia, String tongTien, String ngayNhap, String nguoiNhap, String trangThai) {
        this.maNH = new SimpleStringProperty(maNH);
        this.tenSP = new SimpleStringProperty(tenSP);
        this.nhaCC = new SimpleStringProperty(nhaCC);
        this.soLuong = new SimpleStringProperty(soLuong);
        this.donGia = new SimpleStringProperty(donGia);
        this.tongTien = new SimpleStringProperty(tongTien);
        this.ngayNhap = new SimpleStringProperty(ngayNhap);
        this.nguoiNhap = new SimpleStringProperty(nguoiNhap);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    public StringProperty maNHProperty() { return maNH; }
    public StringProperty tenSPProperty() { return tenSP; }
    public StringProperty nhaCCProperty() { return nhaCC; }
    public StringProperty soLuongProperty() { return soLuong; }
    public StringProperty donGiaProperty() { return donGia; }
    public StringProperty tongTienProperty() { return tongTien; }
    public StringProperty ngayNhapProperty() { return ngayNhap; }
    public StringProperty nguoiNhapProperty() { return nguoiNhap; }
    public StringProperty trangThaiProperty() { return trangThai; }
}