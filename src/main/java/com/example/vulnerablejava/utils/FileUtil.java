package com.example.vulnerablejava.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileUtil {
    public static String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bf;
        File file = new File(path);
        try {
            bf = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
            bf.close();
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
}
