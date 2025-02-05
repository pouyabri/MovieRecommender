package org.example.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ابزار کمکی برای خواندن CSV
 */
public class CsvUtils {
    public static List<String[]> readCsv(String filePath) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // سطر هدر را رد می‌کنیم
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",", -1);
                rows.add(fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
