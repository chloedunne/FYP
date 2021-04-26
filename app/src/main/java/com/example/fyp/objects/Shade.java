package com.example.fyp.objects;

import java.io.Serializable;

public class Shade implements Serializable {

    private String name;
    private String colour;
    private int id;

    public Shade(){}

    public Shade(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    public Shade(String name, String colour, int id) {
        this.name = name;
        this.colour = colour;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
