package org.lager.repository.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;

public class XmlEditor {
    private final String filePath;
    private final XmlMapper xmlMapper;

    public XmlEditor(String filePath) {
        this.filePath = filePath;
        this.xmlMapper = new XmlMapper();
    }

    public void saveToFile(XmlBasketsList baskets) throws IOException {
        xmlMapper.writeValue(new File(filePath), baskets);
    }

    public XmlBasketsList loadFromFile() throws IOException {
        return xmlMapper.readValue(new File(filePath), XmlBasketsList.class);
    }
}

