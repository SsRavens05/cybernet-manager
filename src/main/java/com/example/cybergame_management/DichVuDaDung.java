package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DichVuDaDung {
    private final StringProperty maDVDD;
    private final StringProperty maSP;
    private final StringProperty soLuong;
    private final StringProperty trangThai;
    private final StringProperty thoiGian;

    public DichVuDaDung(String maDVDD, String maSP, String soLuong, String trangThai, String thoiGian) {
        this.maDVDD = new SimpleStringProperty(maDVDD);
        this.maSP = new SimpleStringProperty(maSP);
        this.soLuong = new SimpleStringProperty(soLuong);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.thoiGian = new SimpleStringProperty(thoiGian);
    }

    public StringProperty maDVDDProperty() { return maDVDD; }
    public StringProperty maSPProperty() { return maSP; }
    public StringProperty soLuongProperty() { return soLuong; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty thoiGianProperty() { return thoiGian; }
}
