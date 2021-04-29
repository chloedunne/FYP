package com.example.fyp.objects;

import com.example.fyp.objects.Product;

import java.util.ArrayList;

public class BeautyBag {

    String userID;
    ArrayList<Product> products = new ArrayList<Product>();

    public BeautyBag(){
    }
    

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void addProduct(Product p){
        products.add(p);
    }

    public void removeProduct(Product p){
        products.remove(p);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
