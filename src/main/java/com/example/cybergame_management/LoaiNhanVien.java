package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoaiNhanVien {
    private final StringProperty maLoaiNV;
    private final StringProperty viTri;
    private final StringProperty mucLuong;

    public LoaiNhanVien(String maLoaiNV, String viTri, String mucLuong) {
        this.maLoaiNV = new SimpleStringProperty(maLoaiNV);
        this.viTri = new SimpleStringProperty(viTri);
        this.mucLuong = new SimpleStringProperty(mucLuong);
    }

    // --- PROPERTY ---
    public StringProperty maLoaiNVProperty() { return maLoaiNV; }
    public StringProperty viTriProperty() { return viTri; }
    public StringProperty mucLuongProperty() { return mucLuong; }

    // --- GETTER ---
    public String getMaLoaiNV() { return maLoaiNV.get(); }
    public String getViTri() { return viTri.get(); }
    public String getMucLuong() { return mucLuong.get(); }

    // --- SETTER ---
    public void setMaLoaiNV(String value) { maLoaiNV.set(value); }
    public void setViTri(String value) { viTri.set(value); }
    public void setMucLuong(String value) { mucLuong.set(value); }
}
