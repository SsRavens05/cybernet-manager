package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KhuyenMai {
    private final StringProperty maCTR;
    private final StringProperty tenCTR;
    private final StringProperty loaiCTR;
    private final StringProperty chietKhau;
    private final StringProperty ngayBD;
    private final StringProperty ngayKT;
    private final StringProperty trangThai;

    public KhuyenMai(String maCTR, String tenCTR, String loaiCTR, String chietKhau,
                     String ngayBD, String ngayKT, String trangThai) {
        this.maCTR = new SimpleStringProperty(maCTR);
        this.tenCTR = new SimpleStringProperty(tenCTR);
        this.loaiCTR = new SimpleStringProperty(loaiCTR);
        this.chietKhau = new SimpleStringProperty(chietKhau);
        this.ngayBD = new SimpleStringProperty(ngayBD);
        this.ngayKT = new SimpleStringProperty(ngayKT);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    // --- PROPERTY METHODS ---
    public StringProperty maCTRProperty() { return maCTR; }
    public StringProperty tenCTRProperty() { return tenCTR; }
    public StringProperty loaiCTRProperty() { return loaiCTR; }
    public StringProperty chietKhauProperty() { return chietKhau; }
    public StringProperty ngayBDProperty() { return ngayBD; }
    public StringProperty ngayKTProperty() { return ngayKT; }
    public StringProperty trangThaiProperty() { return trangThai; }

    // --- GETTER METHODS ---
    public String getMaCTR() { return maCTR.get(); }
    public String getTenCTR() { return tenCTR.get(); }
    public String getLoaiCTR() { return loaiCTR.get(); }
    public String getChietKhau() { return chietKhau.get(); }
    public String getNgayBD() { return ngayBD.get(); }
    public String getNgayKT() { return ngayKT.get(); }
    public String getTrangThai() { return trangThai.get(); }

    // --- SETTER METHODS ---
    public void setMaCTR(String value) { maCTR.set(value); }
    public void setTenCTR(String value) { tenCTR.set(value); }
    public void setLoaiCTR(String value) { loaiCTR.set(value); }
    public void setChietKhau(String value) { chietKhau.set(value); }
    public void setNgayBD(String value) { ngayBD.set(value); }
    public void setNgayKT(String value) { ngayKT.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
}
