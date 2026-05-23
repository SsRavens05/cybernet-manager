package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DoiQua {
    private final StringProperty maDQ;
    private final StringProperty maKH;
    private final StringProperty maQT;
    private final StringProperty ngayDoi;
    private final StringProperty soLuong;
    private final StringProperty trangThai;

    public DoiQua(String maDQ, String maKH, String maQT, String ngayDoi, String soLuong, String trangThai) {
        this.maDQ = new SimpleStringProperty(maDQ);
        this.maKH = new SimpleStringProperty(maKH);
        this.maQT = new SimpleStringProperty(maQT);
        this.ngayDoi = new SimpleStringProperty(ngayDoi);
        this.soLuong = new SimpleStringProperty(soLuong);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    public StringProperty maDQProperty() { return maDQ; }
    public StringProperty maKHProperty() { return maKH; }
    public StringProperty maQTProperty() { return maQT; }
    public StringProperty ngayDoiProperty() { return ngayDoi; }
    public StringProperty soLuongProperty() { return soLuong; }
    public StringProperty trangThaiProperty() { return trangThai; }

    public String getMaDQ() { return maDQ.get(); }
    public void setMaDQ(String value) { maDQ.set(value); }

    public String getMaKH() { return maKH.get(); }
    public void setMaKH(String value) { maKH.set(value); }

    public String getMaQT() { return maQT.get(); }
    public void setMaQT(String value) { maQT.set(value); }

    public String getNgayDoi() { return ngayDoi.get(); }
    public void setNgayDoi(String value) { ngayDoi.set(value); }

    public String getSoLuong() { return soLuong.get(); }
    public void setSoLuong(String value) { soLuong.set(value); }

    public String getTrangThai() { return trangThai.get(); }
    public void setTrangThai(String value) { trangThai.set(value); }
}
