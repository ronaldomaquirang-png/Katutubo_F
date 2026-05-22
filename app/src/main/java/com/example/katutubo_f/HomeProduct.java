package com.example.katutubo_f;

public class HomeProduct {
    public String name, price, description, category, artisan, materials, inspiration, history;
    public int imageResource;
    public HomeProduct(String name, String price, String description, String category, int imageResource, String artisan, String materials, String inspiration, String history) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.imageResource = imageResource;
        this.artisan = artisan;
        this.materials = materials;
        this.inspiration = inspiration;
        this.history = history;
    }
}
