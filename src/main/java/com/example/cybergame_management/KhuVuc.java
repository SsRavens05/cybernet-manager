package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KhuVuc {
    private final StringProperty maKV;
    private final StringProperty tenKV;
    private final StringProperty soMay;
    private final StringProperty giaThue;
    private final StringProperty trangThai;
    private final StringProperty moTa;

    public KhuVuc(String maKV, String tenKV, String soMay, String giaThue, String trangThai, String moTa) {
        this.maKV = new SimpleStringProperty(maKV);
        this.tenKV = new SimpleStringProperty(tenKV);
        this.soMay = new SimpleStringProperty(soMay);
        this.giaThue = new SimpleStringProperty(giaThue);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.moTa = new SimpleStringProperty(moTa);
    }

    public StringProperty maKVProperty() { return maKV; }
    public StringProperty tenKVProperty() { return tenKV; }
    public StringProperty soMayProperty() { return soMay; }
    public StringProperty giaThueProperty() { return giaThue; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty moTaProperty() { return moTa; }

    public String getMaKV() { return maKV.get(); }
    public String getTenKV() { return tenKV.get(); }
    public String getSoMay() { return soMay.get(); }
    public String getGiaThue() { return giaThue.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getMoTa() { return moTa.get(); }

    public void setMaKV(String value) { maKV.set(value); }
    public void setTenKV(String value) { tenKV.set(value); }
    public void setSoMay(String value) { soMay.set(value); }
    public void setGiaThue(String value) { giaThue.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setMoTa(String value) { moTa.set(value); }
}
