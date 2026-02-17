package stima.utils;

import stima.model.Board;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputLoader {

    public static Board loadBoard(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return processLines(lines);
    }

    public static Board loadBoardFromString(String content) throws IOException {
        if (content == null || content.trim().isEmpty()) {
            throw new IOException("Input teks kosong!");
        }
        String[] splitLines = content.split("\\r?\\n");
        List<String> lines = new ArrayList<>();
        for (String line : splitLines) {
            if (!line.trim().isEmpty()) {
                lines.add(line.trim());
            }
        }
        return processLines(lines);
    }

    private static Board processLines(List<String> lines) throws IOException {
        if (lines.isEmpty()) {
            throw new IOException("Data kosong atau tidak terbaca!");
        }

        int size = lines.size();
        char[][] region_map = new char[size][size];

        for (int i=0; i<size; i++) {
            String current_row = lines.get(i);
            
            for (char c : current_row.toCharArray()) {
                if (c < 'A' || c > 'Z') {
                    throw new IOException("File tidak valid: Ditemukan karakter ilegal '" + c + "' pada baris " + (i + 1) + ".\n" + "Pastikan file hanya berisi huruf kapital A-Z (Format .txt murni).");
                }
            }
            
            if (current_row.length() != size) {
                throw new IOException("Input tidak valid: Baris ke-" + (i+ 1) + " memiliki panjang " + current_row.length() + ", seharusnya " + size + ".\nPastikan papan berbentuk persegi.");
            }

            for (int j=0; j<size; j++) {
                region_map[i][j] = current_row.charAt(j);
            }
        }
        return new Board(size, region_map);
    }
}