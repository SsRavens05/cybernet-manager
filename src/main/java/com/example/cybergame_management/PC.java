package com.example.cybergame_management;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PC {
    private final StringProperty maPC;
    private final StringProperty maKV;
    private final StringProperty cpu;
    private final StringProperty ram;
    private final StringProperty vga;
    private final StringProperty rom;
    private final StringProperty soMay;
    private final StringProperty loaiPC;
    private final StringProperty trangThai;
    private final StringProperty createAt;

    public PC(String maPC, String maKV, String cpu, String ram, String vga, String rom, String soMay, String loaiPC, String trangThai, String createAt) {
        this.maPC = new SimpleStringProperty(maPC);
        this.maKV = new SimpleStringProperty(maKV);
        this.cpu = new SimpleStringProperty(cpu);
        this.ram = new SimpleStringProperty(ram);
        this.vga = new SimpleStringProperty(vga);
        this.rom = new SimpleStringProperty(rom);
        this.soMay = new SimpleStringProperty(soMay);
        this.loaiPC = new SimpleStringProperty(loaiPC);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.createAt = new SimpleStringProperty(createAt);
    }

    // --- Properties ---
    public StringProperty maPCProperty() { return maPC; }
    public StringProperty maKVProperty() { return maKV; }
    public StringProperty cpuProperty() { return cpu; }
    public StringProperty ramProperty() { return ram; }
    public StringProperty vgaProperty() { return vga; }
    public StringProperty romProperty() { return rom; }
    public StringProperty soMayProperty() { return soMay; }
    public StringProperty loaiPCProperty() { return loaiPC; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty createAtProperty() { return createAt; }

    // --- Getters ---
    public String getMaPC() { return maPC.get(); }
    public String getMaKV() { return maKV.get(); }
    public String getCpu() { return cpu.get(); }
    public String getRam() { return ram.get(); }
    public String getVga() { return vga.get(); }
    public String getRom() { return rom.get(); }
    public String getSoMay() { return soMay.get(); }
    public String getLoaiPC() { return loaiPC.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getCreateAt() { return createAt.get(); }

    // --- Setters ---
    public void setMaPC(String value) { maPC.set(value); }
    public void setMaKV(String value) { maKV.set(value); }
    public void setCpu(String value) { cpu.set(value); }
    public void setRam(String value) { ram.set(value); }
    public void setVga(String value) { vga.set(value); }
    public void setRom(String value) { rom.set(value); }
    public void setSoMay(String value) { soMay.set(value); }
    public void setLoaiPC(String value) { loaiPC.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setCreateAt(String value) { createAt.set(value); }
}
