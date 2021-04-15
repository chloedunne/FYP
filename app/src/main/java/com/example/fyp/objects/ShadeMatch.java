package com.example.fyp.objects;

public class ShadeMatch {

    String brand;
    String product;
    String hex;

    public ShadeMatch(){}

    public ShadeMatch(String brand, String product, String hex) {
        this.brand = brand;
        this.product = product;
        this.hex = hex;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }
}
