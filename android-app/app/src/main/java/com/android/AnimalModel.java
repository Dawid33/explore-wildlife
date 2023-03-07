package com.android;

/**
 * Represents and holds everything an animal contains in the bestiary screen
 */
public class AnimalModel {
    private String name;
    private int witnessedInstances;

    private int image;

    public AnimalModel(String name, int witnessedInstances, int image) {
        this.name = name;
        this.witnessedInstances = witnessedInstances;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getWitnessedInstances() {
        return witnessedInstances;
    }

    public int getImage() {
        return image;
    }
}
