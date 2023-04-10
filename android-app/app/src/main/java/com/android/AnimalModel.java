package com.android;

import android.graphics.drawable.Drawable;

/**
 * Represents and holds everything an animal contains in the bestiary screen
 */
public class AnimalModel {
    private String name;
    private int witnessedInstances;

    private int image;



    private Drawable drawableImage;

    public AnimalModel(String name, int witnessedInstances, int image) {
        this.name = name;
        this.witnessedInstances = witnessedInstances;
        this.image = image;
    }

    public AnimalModel(String name, int witnessedInstances, Drawable image) {
        this.name = name;
        this.witnessedInstances = witnessedInstances;
        this.drawableImage = image;
    }

    public AnimalModel(String name) {
        this.name = name;
        this.witnessedInstances = 0;
        this.image = 0;
    }

    public String getName() {
        return name;
    }

    public void setWitnessedInstances(int value) {
        this.witnessedInstances = value;
    }

    public int getWitnessedInstances() {
        return witnessedInstances;
    }

    public int getImage() {
        return image;
    }

    public Drawable getDrawableImage() {
        return drawableImage;
    }
}
