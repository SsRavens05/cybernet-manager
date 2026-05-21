package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoaiKhuVuc {
    private final StringProperty maLoaiKV;
    private final StringProperty tenLoaiKV;
    private final StringProperty soLuong;
    private final StringProperty gia;
    private final StringProperty ngayTao;

    public LoaiKhuVuc(String maLoaiKV, String tenLoaiKV, String soLuong, String gia, String ngayTao) {
        this.maLoaiKV = new SimpleStringProperty(maLoaiKV);
        this.tenLoaiKV = new SimpleStringProperty(tenLoaiKV);
        this.soLuong = new SimpleStringProperty(soLuong);
        this.gia = new SimpleStringProperty(gia);
        this.ngayTao = new SimpleStringProperty(ngayTao);
    }

    public StringProperty maLoaiKVProperty() { return maLoaiKV; }
    public StringProperty tenLoaiKVProperty() { return tenLoaiKV; }
    public StringProperty soLuongProperty() { return soLuong; }
    public StringProperty giaProperty() { return gia; }
    public StringProperty ngayTaoProperty() { return ngayTao; }

    public String getMaLoaiKV() { return maLoaiKV.get(); }
    public String getTenLoaiKV() { return tenLoaiKV.get(); }
    public String getSoLuong() { return soLuong.get(); }
    public String getGia() { return gia.get(); }
    public String getNgayTao() { return ngayTao.get(); }

    public void setMaLoaiKV(String value) { maLoaiKV.set(value); }
    public void setTenLoaiKV(String value) { tenLoaiKV.set(value); }
    public void setSoLuong(String value) { soLuong.set(value); }
    public void setGia(String value) { gia.set(value); }
    public void setNgayTao(String value) { ngayTao.set(value); }
}
