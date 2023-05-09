package com.example.vulnerablejava.utils;

import java.util.UUID;

public class CSRFUtil {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
