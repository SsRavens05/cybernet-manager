package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SuKien {
    private final StringProperty maSK;
    private final StringProperty tenSK;
    private final StringProperty ngayTC;
    private final StringProperty gioBD;
    private final StringProperty gioKT;
    private final StringProperty soNguoi;
    private final StringProperty giaiThuong;
    private final StringProperty trangThai;

    public SuKien(String maSK, String tenSK, String ngayTC, String gioBD, String gioKT,
                  String soNguoi, String giaiThuong, String trangThai) {
        this.maSK = new SimpleStringProperty(maSK);
        this.tenSK = new SimpleStringProperty(tenSK);
        this.ngayTC = new SimpleStringProperty(ngayTC);
        this.gioBD = new SimpleStringProperty(gioBD);
        this.gioKT = new SimpleStringProperty(gioKT);
        this.soNguoi = new SimpleStringProperty(soNguoi);
        this.giaiThuong = new SimpleStringProperty(giaiThuong);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    public StringProperty maSKProperty() { return maSK; }
    public StringProperty tenSKProperty() { return tenSK; }
    public StringProperty ngayTCProperty() { return ngayTC; }
    public StringProperty gioBDProperty() { return gioBD; }
    public StringProperty gioKTProperty() { return gioKT; }
    public StringProperty soNguoiProperty() { return soNguoi; }
    public StringProperty giaiThuongProperty() { return giaiThuong; }
    public StringProperty trangThaiProperty() { return trangThai; }
}
