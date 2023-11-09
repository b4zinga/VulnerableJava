package com.example.vulnerablejava.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandUtil {
    public static String execute(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            Process p;
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
                p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
            } else {
                p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", command });
            }
            BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }
}
