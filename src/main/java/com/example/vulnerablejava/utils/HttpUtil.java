package com.example.vulnerablejava.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtil {
    public static String doGet(String url) {
        StringBuilder sb = new StringBuilder();
        try {
            URL u = new URL(url);
            URLConnection connection = u.openConnection();
            connection.setConnectTimeout(3000);
            String line;
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((line=bf.readLine()) != null) {
                sb.append(line+"\n");
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
}
