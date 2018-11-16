package com.septiprima.tugasakhir.constructor;

/**
 * Created by yosep on 1/28/2018.
 */

public class Categories {

    String name;
    String images;

    public Categories() {
    }

    public Categories(String name, String images) {
        this.name = name;
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
