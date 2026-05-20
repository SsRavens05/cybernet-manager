package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KhachHang {
    private final StringProperty maKH;
    private final StringProperty hoTen;
    private final StringProperty soDu;
    private final StringProperty soDiemTichLuy;
    private final StringProperty trangThai;
    private final StringProperty ngayDK;

    public KhachHang(String maKH, String hoTen, String soDu, String soDiemTichLuy, String trangThai, String ngayDK) {
        this.maKH = new SimpleStringProperty(maKH);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.soDu = new SimpleStringProperty(soDu);
        this.soDiemTichLuy = new SimpleStringProperty(soDiemTichLuy);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.ngayDK = new SimpleStringProperty(ngayDK);
    }

    public StringProperty maKHProperty() { return maKH; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty soDuProperty() { return soDu; }
    public StringProperty soDiemTichLuyProperty() { return soDiemTichLuy; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty ngayDKProperty() { return ngayDK; }

    public String getMaKH() { return maKH.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getSoDu() { return soDu.get(); }
    public String getSoDiemTichLuy() { return soDiemTichLuy.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getNgayDK() { return ngayDK.get(); }

    public void setMaKH(String value) { maKH.set(value); }
    public void setHoTen(String value) { hoTen.set(value); }
    public void setSoDu(String value) { soDu.set(value); }
    public void setSoDiemTichLuy(String value) { soDiemTichLuy.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setNgayDK(String value) { ngayDK.set(value); }
}
