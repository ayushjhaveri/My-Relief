package com.example.ayush.myrelief;

public class ItemMarker {

    private String title, snippet;
    private double latitude, longitide;

    public ItemMarker(String title, String snippet, double latitude, double longitide) {
        this.title = title;
        this.snippet = snippet;
        this.latitude = latitude;
        this.longitide = longitide;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitide() {
        return longitide;
    }
}
