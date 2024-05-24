package com.example.quanlychitieu;

public class Thu_Chi {
    String id_thu_chi;
    String id_User;
    String id_danh_muc;
    String so_tien;
    String ghi_chu;
    String loai_thu_chi;
    String ngay;
    String name_danh_muc;
    public Thu_Chi(){}


    public Thu_Chi(String id_thu_chi, String id_User, String id_danh_muc, String so_tien, String ghi_chu, String loai_thu_chi, String ngay, String name_danh_muc) {
        this.id_thu_chi = id_thu_chi;
        this.id_User = id_User;
        this.id_danh_muc = id_danh_muc;
        this.so_tien = so_tien;
        this.ghi_chu = ghi_chu;
        this.loai_thu_chi = loai_thu_chi;
        this.ngay = ngay;
        this.name_danh_muc = name_danh_muc;
    }

    public String getId_thu_chi() {
        return id_thu_chi;
    }

    public void setId_thu_chi(String id_thu_chi) {
        this.id_thu_chi = id_thu_chi;
    }

    public String getId_User() {
        return id_User;
    }

    public void setId_User(String id_User) {
        this.id_User = id_User;
    }

    public String getId_danh_muc() {
        return id_danh_muc;
    }

    public void setId_danh_muc(String id_danh_muc) {
        this.id_danh_muc = id_danh_muc;
    }

    public String getSo_tien() {
        return so_tien;
    }

    public void setSo_tien(String so_tien) {
        this.so_tien = so_tien;
    }

    public String getGhi_chu() {
        return ghi_chu;
    }

    public void setGhi_chu(String ghi_chu) {
        this.ghi_chu = ghi_chu;
    }

    public String getLoai_thu_chi() {
        return loai_thu_chi;
    }

    public void setLoai_thu_chi(String loai_thu_chi) {
        this.loai_thu_chi = loai_thu_chi;
    }


    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public String getName_danh_muc() {
        return name_danh_muc;
    }

    public void setName_danh_muc(String name_danh_muc) {
        this.name_danh_muc = name_danh_muc;
    }
}
