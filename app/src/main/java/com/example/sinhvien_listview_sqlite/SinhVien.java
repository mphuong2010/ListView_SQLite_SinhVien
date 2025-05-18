package com.example.sinhvien_listview_sqlite;

import java.io.Serializable;

public class SinhVien implements Serializable {

    private int id;
    private String hoTen;
    private String mssv;
    private String avatarPath;
    private String chuyenNganh;
    private String ngaySinh;
    private String sodienthoai;

    // Constructor
    public SinhVien(int id, String hoTen, String mssv, String avatarPath, String chuyenNganh, String ngaySinh, String sodienthoai) {
        this.id = id;
        this.hoTen = hoTen;
        this.mssv = mssv;
        this.avatarPath = avatarPath;
        this.chuyenNganh = chuyenNganh;
        this.ngaySinh = ngaySinh;
        this.sodienthoai = sodienthoai;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public String getChuyenNganh() { return chuyenNganh; }
    public void setChuyenNganh(String chuyenNganh) { this.chuyenNganh = chuyenNganh; }

    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSodienthoai() { return sodienthoai; }
    public void setSodienthoai(String khoaHoc) { this.sodienthoai = sodienthoai; }
}
