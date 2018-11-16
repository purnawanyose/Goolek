package com.septiprima.tugasakhir.constructor;

/**
 * Created by yosep on 1/29/2018.
 */

public class Store {

    String alamat;
    String pemilik;
    String toko;
    String kota;
    String lat;
    String lng;
    String parkir;
    String category;
    String id;
    String telepon;
    String visitor;

    public Store() {
    }

    public Store(String alamat, String pemilik, String toko, String kota, String lat,
                 String lng, String parkir, String category, String id, String telepon) {
        this.alamat = alamat;
        this.pemilik = pemilik;
        this.toko = toko;
        this.kota = kota;
        this.lat = lat;
        this.lng = lng;
        this.parkir = parkir;
        this.category = category;
        this.id = id;
        this.telepon = telepon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getPemilik() {
        return pemilik;
    }

    public void setPemilik(String pemilik) {
        this.pemilik = pemilik;
    }

    public String getToko() {
        return toko;
    }

    public void setToko(String toko) {
        this.toko = toko;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getParkir() {
        return parkir;
    }

    public void setParkir(String parkir) {
        this.parkir = parkir;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public void setVisitor(String visitor) {
        this.visitor = visitor;
    }

    public String getVisitor() {
        return visitor;
    }
}
