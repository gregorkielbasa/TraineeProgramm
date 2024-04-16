package org.lager.repository.csv;

import java.io.*;
import java.util.List;

public class CsvEditor {
    private final String filePath;
    private final String fileHeader;

    public CsvEditor(String filePath, String fileHeader) {
        this.filePath = filePath;
        this.fileHeader = fileHeader;
    }

    public void saveToFile(List<String> records) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(fileHeader);
        for (String record : records) {
            writer.newLine();
            writer.write(record);
        }
        writer.close();
    }

    public List<String> loadFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<String> records = reader.lines()
                .skip(1)
                .toList();
        reader.close();
        return records;
    }
}
