package com.example.katutubo_f;

public class AdItem {
    public String title, description, category, query;
    public int imageRes;
    public AdItem(String title, String description, int imageRes, String category, String query) {
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
        this.category = category;
        this.query = query;
    }
}
