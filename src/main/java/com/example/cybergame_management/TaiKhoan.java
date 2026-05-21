package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaiKhoan {
    private final StringProperty tenDangNhap;
    private final StringProperty matKhau;
    private final StringProperty vaiTro;
    private final StringProperty trangThai;
    private final StringProperty ngayTao;

    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, String trangThai, String ngayTao) {
        this.tenDangNhap = new SimpleStringProperty(tenDangNhap);
        this.matKhau = new SimpleStringProperty(matKhau);
        this.vaiTro = new SimpleStringProperty(vaiTro);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.ngayTao = new SimpleStringProperty(ngayTao);
    }

    public StringProperty tenDangNhapProperty() { return tenDangNhap; }
    public StringProperty matKhauProperty() { return matKhau; }
    public StringProperty vaiTroProperty() { return vaiTro; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty ngayTaoProperty() { return ngayTao; }

    public String getTenDangNhap() { return tenDangNhap.get(); }
    public String getMatKhau() { return matKhau.get(); }
    public String getVaiTro() { return vaiTro.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getNgayTao() { return ngayTao.get(); }

    public void setTenDangNhap(String value) { tenDangNhap.set(value); }
    public void setMatKhau(String value) { matKhau.set(value); }
    public void setVaiTro(String value) { vaiTro.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setNgayTao(String value) { ngayTao.set(value); }
}
