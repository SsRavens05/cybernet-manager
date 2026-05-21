package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CaLam {
    private final StringProperty maCa;
    private final StringProperty thoiGianBD;
    private final StringProperty thoiGianKT;
    private final StringProperty soGioLam;
    private final StringProperty trangThai;
    private final StringProperty soGioTangCa;

    public CaLam(String maCa, String thoiGianBD, String thoiGianKT, String soGioLam, String trangThai, String soGioTangCa) {
        this.maCa = new SimpleStringProperty(maCa);
        this.thoiGianBD = new SimpleStringProperty(thoiGianBD);
        this.thoiGianKT = new SimpleStringProperty(thoiGianKT);
        this.soGioLam = new SimpleStringProperty(soGioLam);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.soGioTangCa = new SimpleStringProperty(soGioTangCa);
    }

    // --- PROPERTY ---
    public StringProperty maCaProperty() { return maCa; }
    public StringProperty thoiGianBDProperty() { return thoiGianBD; }
    public StringProperty thoiGianKTProperty() { return thoiGianKT; }
    public StringProperty soGioLamProperty() { return soGioLam; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty soGioTangCaProperty() { return soGioTangCa; }

    // --- GETTER ---
    public String getMaCa() { return maCa.get(); }
    public String getThoiGianBD() { return thoiGianBD.get(); }
    public String getThoiGianKT() { return thoiGianKT.get(); }
    public String getSoGioLam() { return soGioLam.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getSoGioTangCa() { return soGioTangCa.get(); }

    // --- SETTER ---
    public void setMaCa(String value) { maCa.set(value); }
    public void setThoiGianBD(String value) { thoiGianBD.set(value); }
    public void setThoiGianKT(String value) { thoiGianKT.set(value); }
    public void setSoGioLam(String value) { soGioLam.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setSoGioTangCa(String value) { soGioTangCa.set(value); }
}
