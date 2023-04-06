package com.example.vulnerablejava.config;

import lombok.Data;

@Data
public class FakeConfig {
    private String host;
    private String port;
    private String username;
    private String password;
}
