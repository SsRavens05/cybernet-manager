package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KhuyenMai {
    private final StringProperty maKM;
    private final StringProperty tenKM;
    private final StringProperty loai;
    private final StringProperty giaTri;
    private final StringProperty dieuKien;
    private final StringProperty ngayBD;
    private final StringProperty ngayKT;
    private final StringProperty trangThai;

    public KhuyenMai(String maKM, String tenKM, String loai, String giaTri, String dieuKien,
                     String ngayBD, String ngayKT, String trangThai) {
        this.maKM = new SimpleStringProperty(maKM);
        this.tenKM = new SimpleStringProperty(tenKM);
        this.loai = new SimpleStringProperty(loai);
        this.giaTri = new SimpleStringProperty(giaTri);
        this.dieuKien = new SimpleStringProperty(dieuKien);
        this.ngayBD = new SimpleStringProperty(ngayBD);
        this.ngayKT = new SimpleStringProperty(ngayKT);
        this.trangThai = new SimpleStringProperty(trangThai);
    }

    public StringProperty maKMProperty() { return maKM; }
    public StringProperty tenKMProperty() { return tenKM; }
    public StringProperty loaiProperty() { return loai; }
    public StringProperty giaTriProperty() { return giaTri; }
    public StringProperty dieuKienProperty() { return dieuKien; }
    public StringProperty ngayBDProperty() { return ngayBD; }
    public StringProperty ngayKTProperty() { return ngayKT; }
    public StringProperty trangThaiProperty() { return trangThai; }
}
