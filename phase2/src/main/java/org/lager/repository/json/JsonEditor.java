package org.lager.repository.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonEditor {

    private String filePath;
    private ObjectMapper objectMapper;

    public JsonEditor(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void saveToFile(List<JsonOrder> records) throws IOException {
        objectMapper.writeValue(new File(filePath), records);
    }

    public List<JsonOrder> loadFromFile() throws IOException {
        return objectMapper.readValue(new File(filePath), new TypeReference<List<JsonOrder>>(){});
    }
}
