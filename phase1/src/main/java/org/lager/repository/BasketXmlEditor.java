package org.lager.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import org.lager.model.Basket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasketXmlEditor {
    private final String filePath;
    private final XmlMapper xmlMapper;

    public BasketXmlEditor(String filePath) {
        xmlMapper = new XmlMapper();
        this.filePath = filePath;
    }

    public class BasketList {
        public record BasketRecord(long customerNumber, Map<Long, Integer> products) {
            Basket getBasket() {
                Basket result = new Basket(customerNumber);
                products.forEach(result::insert);
                return result;
            }
        }

        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonProperty("Basket")
        public List<BasketRecord> baskets;

        public BasketList() {
            this.baskets = new ArrayList<>();
        }

        public BasketList(List<Basket> baskets) {
            this.baskets = new ArrayList<>(baskets.size());

            for (Basket basket : baskets) {
                this.baskets.add(new BasketRecord(basket.getCustomerNumber(), basket.getContent()));
            }
        }

       List<Basket> getBaskets() {
            List<Basket> result = new ArrayList<>(baskets.size());
            baskets.forEach(record -> result.add(record.getBasket()));
            return result;
        }
    }

    public void saveToFile(List<Basket> baskets) throws IOException {
        xmlMapper.writeValue(new File(filePath), new BasketList(baskets));
    }

    public List<Basket> loadFromFile() throws IOException {
        BasketList basketList = xmlMapper.readValue(new File(filePath), BasketList.class);

        return basketList.getBaskets();
    }
}
