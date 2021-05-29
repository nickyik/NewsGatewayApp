package com.example.newsgateway;

import java.io.Serializable;

public class News implements Serializable {
    private String id;
    private String name;
    private String category;

    public News(){
        id="";
        name="";
        category="";
    }

    public News(String id, String name, String category){
        this.id = id;
        this.name = name;
        this.category = category;
    }

    //getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

}
