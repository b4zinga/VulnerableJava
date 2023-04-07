package com.example.vulnerablejava.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image implements Serializable{
    private String url;
    private String name;

    public Image() {}

    public Image(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
