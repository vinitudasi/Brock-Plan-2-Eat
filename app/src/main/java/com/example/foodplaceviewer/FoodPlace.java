package com.example.foodplaceviewer;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

// Represents an individual food place
// Note: add a string array to separate tags

public class FoodPlace {

    private String name, location, websiteURL, imageURL, imageURL2, tags;
    private long id;
    private int favorite;


    public FoodPlace(long id, String name, String location, String websiteURL, String imageURL, String imageURL2, String tags, int favorite) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.websiteURL = websiteURL;
        this.imageURL = imageURL;
        this.imageURL2 = imageURL2;
        this.tags = tags;
        this.favorite = favorite;

    }

    public FoodPlace() {
    }

    public long getId(){ return id; }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public String getImageURL2() {return imageURL2;}

    public String getImageURL() {
        return imageURL;
    }

    public String getTags() {
        return tags;
    }

    public int getFavorite() {
        return favorite;
    }
}
