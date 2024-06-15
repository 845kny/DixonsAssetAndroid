package com.example.dixonsasset.Kru.CekGudang;

public class CekGudangModel {
    private String id;
    private String nama;
    private String kategori;
    private String file;
    private String deskripsi;
    private String status;
    private String namapeminjam;
    private String tanggalpeminjam;

    private String lokasi;
    public CekGudangModel() {
    }

    public CekGudangModel(String id, String nama, String kategori, String file, String deskripsi, String status, String namapeminjam, String tanggalpeminjam, String lokasi) {
        this.id = id;
        this.nama = nama;
        this.lokasi = lokasi;
        this.kategori = kategori;
        this.file = file;
        this.deskripsi = deskripsi;
        this.status = status;
        this.namapeminjam = namapeminjam;
        this.tanggalpeminjam = tanggalpeminjam;
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

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamapeminjam() {
        return namapeminjam;
    }

    public void setNamapeminjam(String namapeminjam) {
        this.namapeminjam = namapeminjam;
    }

    public String getTanggalpeminjam() {
        return tanggalpeminjam;
    }

    public void setTanggalpeminjam(String tanggalpeminjam) {
        this.tanggalpeminjam = tanggalpeminjam;
    }
}
