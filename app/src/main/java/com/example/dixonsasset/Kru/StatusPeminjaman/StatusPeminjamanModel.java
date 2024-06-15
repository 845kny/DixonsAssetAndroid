package com.example.dixonsasset.Kru.StatusPeminjaman;

public class StatusPeminjamanModel {
    private String id;
    private String nama;
    private String file;

    private String durasi;
    public StatusPeminjamanModel() {
    }

    public StatusPeminjamanModel(String id, String nama, String file,String durasi) {
        this.id = id;
        this.nama = nama;
        this.file = file;
        this.durasi = durasi;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
