package com.example.quanlychitieu;

public class Danh_Muc {
    String id_Danh_Muc;
    String id_User;
    String ten_Danh_Muc;
    String icon_Danh_Muc;
    String loai_Danh_Muc;
    public Danh_Muc(){

    }

    public Danh_Muc(String id_Danh_Muc, String id_User, String ten_Danh_Muc, String icon_Danh_Muc, String loai_Danh_Muc) {
        this.id_Danh_Muc = id_Danh_Muc;
        this.id_User = id_User;
        this.ten_Danh_Muc = ten_Danh_Muc;
        this.icon_Danh_Muc = icon_Danh_Muc;
        this.loai_Danh_Muc = loai_Danh_Muc;
    }

    public String getId_User() {
        return id_User;
    }

    public void setId_User(String id_User) {
        this.id_User = id_User;
    }

    public String getId_Danh_Muc() {
        return id_Danh_Muc;
    }

    public void setId_Danh_Muc(String id_Danh_Muc) {
        this.id_Danh_Muc = id_Danh_Muc;
    }

    public String getTen_Danh_Muc() {
        return ten_Danh_Muc;
    }

    public void setTen_Danh_Muc(String ten_Danh_Muc) {
        this.ten_Danh_Muc = ten_Danh_Muc;
    }

    public String getIcon_Danh_Muc() {
        return icon_Danh_Muc;
    }

    public void setIcon_Danh_Muc(String icon_Danh_Muc) {
        this.icon_Danh_Muc = icon_Danh_Muc;
    }

    public String getLoai_Danh_Muc() {
        return loai_Danh_Muc;
    }

    public void setLoai_Danh_Muc(String loai_Danh_Muc) {
        this.loai_Danh_Muc = loai_Danh_Muc;
    }
}
