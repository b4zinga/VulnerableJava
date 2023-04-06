package com.example.vulnerablejava.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {
    private String url;
    private String name;

    public Image() {}

    public Image(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
