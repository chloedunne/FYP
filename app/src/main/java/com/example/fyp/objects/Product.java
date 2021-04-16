package com.example.fyp.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

    private String brand;
    private String name;
    private String description;
    private String productType;
    private Shade shade;
    private double price;
    private ArrayList<Shade> shades;
    private String img;
    private int id;

    public Product(){}

    public Product(String brand, String name, String description, String productType, String img, ArrayList<Shade> shades, int id, double price) {
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.img = img;
        this.shades = shades;
        this.id = id;
        this.price = price;
    }

    public Product(String brand, String name, String description, String productType, String img, Shade shade, int id, double price) {
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.img = img;
        this.shade = shade;
        this.id = id;
        this.price = price;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public ArrayList<Shade> getShades() {
        return shades;
    }

    public void setShades(ArrayList<Shade> shade) {
        this.shades = shades;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Shade getShade() {
        return shade;
    }

    public void setShade(Shade shade) {
        this.shade = shade;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
