package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NhapHang {
    private final StringProperty maPN;
    private final StringProperty loaiHang;
    private final StringProperty nhaCC;
    private final StringProperty soLuong;
    private final StringProperty donGia;
    private final StringProperty tongTienNhap;
    private final StringProperty ngayNhap;
    private final StringProperty nguoiNhap;
    private final StringProperty trangThai;

    public NhapHang(String maPN, String loaiHang, String nhaCC, String soLuong, String donGia, String tongTienNhap, String ngayNhap, String nguoiNhap, String trangThai) {
        this.maPN = new SimpleStringProperty(maPN);
        this.loaiHang = new SimpleStringProperty(loaiHang);
        this.nhaCC = new SimpleStringProperty(nhaCC);
        this.soLuong = new SimpleStringProperty(soLuong);
        this.donGia = new SimpleStringProperty(donGia);
        this.tongTienNhap = new SimpleStringProperty(tongTienNhap);
        this.ngayNhap = new SimpleStringProperty(ngayNhap);
        this.nguoiNhap = new SimpleStringProperty(nguoiNhap);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    public StringProperty maPNProperty() { return maPN; }
    public StringProperty loaiHangProperty() { return loaiHang; }
    public StringProperty nhaCCProperty() { return nhaCC; }
    public StringProperty soLuongProperty() { return soLuong; }
    public StringProperty donGiaProperty() { return donGia; }
    public StringProperty tongTienNhapProperty() { return tongTienNhap; }
    public StringProperty ngayNhapProperty() { return ngayNhap; }
    public StringProperty nguoiNhapProperty() { return nguoiNhap; }
    public StringProperty trangThaiProperty() { return trangThai; }

    public String getMaPN() { return maPN.get(); }
    public String getLoaiHang() { return loaiHang.get(); }
    public String getNhaCC() { return nhaCC.get(); }
    public String getSoLuong() { return soLuong.get(); }
    public String getDonGia() { return donGia.get(); }
    public String getTongTienNhap() { return tongTienNhap.get(); }
    public String getNgayNhap() { return ngayNhap.get(); }
    public String getNguoiNhap() { return nguoiNhap.get(); }
    public String getTrangThai() { return trangThai.get(); }

    public void setMaPN(String value) { maPN.set(value); }
    public void setLoaiHang(String value) { loaiHang.set(value); }
    public void setNhaCC(String value) { nhaCC.set(value); }
    public void setSoLuong(String value) { soLuong.set(value); }
    public void setDonGia(String value) { donGia.set(value); }
    public void setTongTienNhap(String value) { tongTienNhap.set(value); }
    public void setNgayNhap(String value) { ngayNhap.set(value); }
    public void setNguoiNhap(String value) { nguoiNhap.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
}
