package com.example.fyp.objects;

import java.io.Serializable;

public class Shade implements Serializable {

    private String name;
    private String colour;

    public Shade(){}

    public Shade(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
