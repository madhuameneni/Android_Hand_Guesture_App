package com.example.summer_vacation.model;

public class SummerVacationItems {

public SummerVacationItems() {

    }

    public String name;
    public String rating;
    public String imageURL;
    public String info;
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

}
