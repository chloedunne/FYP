package com.example.fyp.objects;

import com.example.fyp.objects.Product;

import java.io.Serializable;

public class Review implements Serializable {

    private Product product;
    private String userID;
    private String description;
    private double review;

    public Review(){}

    public Review(Product product, String userID, String description, double review) {
        this.product = product;
        this.userID = userID;
        this.description = description;
        this.review = review;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getReview() {
        return review;
    }

    public void setReview(double review) {
        this.review = review;
    }
}
