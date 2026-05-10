package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KhachHang {
    private final StringProperty maKH;
    private final StringProperty hoTen;
    private final StringProperty sdt;
    private final StringProperty email;
    private final StringProperty soDu;
    private final StringProperty hang;
    private final StringProperty trangThai;
    private final StringProperty ngayDK;

    public KhachHang(String maKH, String hoTen, String sdt, String email, String soDu, String hang, String trangThai, String ngayDK) {
        this.maKH = new SimpleStringProperty(maKH);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.sdt = new SimpleStringProperty(sdt);
        this.email = new SimpleStringProperty(email);
        this.soDu = new SimpleStringProperty(soDu);
        this.hang = new SimpleStringProperty(hang);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.ngayDK = new SimpleStringProperty(ngayDK);
    }

    // Getters cho TableView
    public StringProperty maKHProperty() { return maKH; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty sdtProperty() { return sdt; }
    public StringProperty emailProperty() { return email; }
    public StringProperty soDuProperty() { return soDu; }
    public StringProperty hangProperty() { return hang; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty ngayDKProperty() { return ngayDK; }
}