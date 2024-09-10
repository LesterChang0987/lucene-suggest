package com.example.lucenesuggest;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author binbin.hou
 * @since 1.0.0
 */
@Data
public class Product implements Serializable {

    private String name;
    private String image;
    private String[] regions;
    private int numberSold;

    public Product(String name, String image, String[] regions, int numberSold) {
        this.name = name;
        this.image = image;
        this.regions = regions;
        this.numberSold = numberSold;
    }

    //getter setter toString

}