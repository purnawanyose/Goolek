package com.septiprima.tugasakhir.constructor;

/**
 * Created by yosep on 2/4/2018.
 */

public class StoreItems {

    String name;
    String id_store;
    String image;
    String id;
    String price;

    public StoreItems() {
    }

    public StoreItems(String name, String id_store, String image, String id, String price) {
        this.name = name;
        this.id_store = id_store;
        this.image = image;
        this.id = id;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_store() {
        return id_store;
    }

    public void setId_store(String id_store) {
        this.id_store = id_store;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
