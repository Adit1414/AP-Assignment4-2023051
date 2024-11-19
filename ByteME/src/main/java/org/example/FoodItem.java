package org.example;

import java.util.ArrayList;
import java.util.List;



public class FoodItem {
    private final String name;
    private int price;
    private boolean isAvailable;
    private String category;
    private final List<Review> reviews;
    private float rating;

    public FoodItem(String name, int price, String category){
        this.name=name;
        this.price=price;
        this.category=category;
        this.isAvailable=true;
        this.reviews=new ArrayList<>();
        this.rating = 0;
    }

    public String getName(){
        return this.name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean availability) {
        isAvailable = availability;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRating(){
        return rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review){
        this.reviews.add(review);
        float ratingSum =0;
        float ratingNum =reviews.size();
        for (Review r : reviews){
            ratingSum +=r.getRating();
        }
        this.rating= ratingSum / ratingNum;
        System.out.println("Review added.");
    }

    @Override
    public String toString(){
        return  "\nName: " + this.name +
                "\nPrice: " + String.valueOf(this.price) +
                "\nIs Available: " + String.valueOf(this.isAvailable) +
                "\nCategory: " + this.category;
    }
}
