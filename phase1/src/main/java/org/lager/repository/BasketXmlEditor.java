package org.lager.repository;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.lager.model.Basket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasketXmlEditor {

    private final String filePath;

    public BasketXmlEditor(String filePath) {
        this.filePath = filePath;
    }

    private class BasketList {
        public record BasketRecord(long customerNumber, Map<Long, Integer> products) {
        }

        public List<BasketRecord> baskets;

        public BasketList(List<Basket> baskets) {
            this.baskets = new ArrayList<>(baskets.size());

            for (Basket basket : baskets) {
                this.baskets.add(new BasketRecord(basket.getCustomerNumber(), basket.getContent()));
            }
        }
    }

    public void saveToFile (List<Basket> baskets) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(new File(filePath), new BasketList(baskets));
    }

    public List<Basket> loadFromFile() throws IOException {
        return null;
    }
}
