package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ThietBi {
    private final StringProperty maTB;
    private final StringProperty tenTB;
    private final StringProperty loaiTB;
    private final StringProperty trangThai;
    private final StringProperty ngayMua;

    public ThietBi(String maTB, String tenTB, String loaiTB, String trangThai, String ngayMua) {
        this.maTB = new SimpleStringProperty(maTB);
        this.tenTB = new SimpleStringProperty(tenTB);
        this.loaiTB = new SimpleStringProperty(loaiTB);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.ngayMua = new SimpleStringProperty(ngayMua);
    }

    // --- PROPERTY ---
    public StringProperty maTBProperty() { return maTB; }
    public StringProperty tenTBProperty() { return tenTB; }
    public StringProperty loaiTBProperty() { return loaiTB; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty ngayMuaProperty() { return ngayMua; }

    // --- GETTER ---
    public String getMaTB() { return maTB.get(); }
    public String getTenTB() { return tenTB.get(); }
    public String getLoaiTB() { return loaiTB.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getNgayMua() { return ngayMua.get(); }

    // --- SETTER ---
    public void setMaTB(String value) { maTB.set(value); }
    public void setTenTB(String value) { tenTB.set(value); }
    public void setLoaiTB(String value) { loaiTB.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setNgayMua(String value) { ngayMua.set(value); }
}