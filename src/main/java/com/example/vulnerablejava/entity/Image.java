package com.example.vulnerablejava.entity;

import java.io.Serializable;

import com.example.vulnerablejava.annotation.ImageName;
import com.example.vulnerablejava.annotation.ImageURL;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image implements Serializable{
    @ImageURL
    private String url;
    @ImageName
    private String name;

    public Image() {}

    public Image(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
