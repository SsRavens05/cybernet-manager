package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QuaTang {
    private final StringProperty maQT;
    private final StringProperty noiDung;
    private final StringProperty soDiemTieuHao;

    public QuaTang(String maQT, String noiDung, String soDiemTieuHao) {
        this.maQT = new SimpleStringProperty(maQT);
        this.noiDung = new SimpleStringProperty(noiDung);
        this.soDiemTieuHao = new SimpleStringProperty(soDiemTieuHao);
    }

    public StringProperty maQTProperty() { return maQT; }
    public StringProperty noiDungProperty() { return noiDung; }
    public StringProperty soDiemTieuHaoProperty() { return soDiemTieuHao; }

    public String getMaQT() { return maQT.get(); }
    public void setMaQT(String value) { maQT.set(value); }

    public String getNoiDung() { return noiDung.get(); }
    public void setNoiDung(String value) { noiDung.set(value); }

    public String getSoDiemTieuHao() { return soDiemTieuHao.get(); }
    public void setSoDiemTieuHao(String value) { soDiemTieuHao.set(value); }
}
