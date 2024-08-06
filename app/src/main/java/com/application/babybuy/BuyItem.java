package com.application.babybuy;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class BuyItem implements Serializable {

    @Exclude
    private String key;
    private String userId;
    private String title;
    private String description;
    private String price;
    private boolean is_purchased;
    private String lat,lng;
    private String pimage;
    private String created_at, updated_at;

    public BuyItem(){}

    public BuyItem(String userId, String title, String description, String price, boolean is_purchased, String lat, String lng, String pimage, String created_at, String updated_at) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.is_purchased = is_purchased;
        this.lat = lat;
        this.lng = lng;
        this.pimage = pimage;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isIs_purchased() {
        return is_purchased;
    }

    public void setIs_purchased(boolean is_purchased) {
        this.is_purchased = is_purchased;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
