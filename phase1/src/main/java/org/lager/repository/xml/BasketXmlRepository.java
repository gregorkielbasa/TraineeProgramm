package org.lager.repository.xml;

import org.lager.exception.RepositoryException;
import org.lager.model.Basket;
import org.lager.repository.BasketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class BasketXmlRepository implements BasketRepository {

    private final XmlEditor xmlEditor;
    private final BasketXmlMapper xmlMapper;

    private final Map<Long, Basket> baskets;
    private final Logger logger = LoggerFactory.getLogger(BasketXmlRepository.class);

    public BasketXmlRepository(XmlEditor xmlEditor, BasketXmlMapper xmlMapper) {
        this.xmlEditor = xmlEditor;
        this.xmlMapper = xmlMapper;
        this.baskets = new HashMap<>();
        loadBasketsFromFile();
    }

    @Override
    public Optional<Basket> read(Long number) {
        validateNumber(number);
        return Optional.ofNullable(baskets.get(number));
    }

    private void validateNumber(Long number) {
        if (number == null)
            throw new RepositoryException("Given Number is NULL");
    }

    @Override
    public void save(Basket basket) throws RepositoryException {
        validateBasket(basket);
        baskets.put(basket.getCustomerNumber(), basket);
        saveBasketsToFile();
    }

    private void validateBasket(Basket basket) throws RepositoryException {
        if (basket == null)
            throw new RuntimeException("Given Basket is NULL");
    }

    @Override
    public void delete(Long number) throws RepositoryException {
        validateNumber(number);
        baskets.remove(number);
        saveBasketsToFile();
    }

    private void saveBasketsToFile() {
//        List<XmlBasket> xmlRecords = baskets.values().stream()
//                .map(xmlMapper::basketToXmlRecord)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .toList();

//        try {
//            xmlEditor.saveToFile(xmlRecords);
//        } catch (IOException e) {
//            logger.error("Basket Repository was not able to save XML File");
//            throw new RepositoryException("BasketRepository was not able to save changes in XML File");
//        }
    }

    private void loadBasketsFromFile() {
//        List<XmlBasket> xmlRecords = new ArrayList<>();
//
////        try {
////            xmlRecords = xmlEditor.loadFromFile();
////            logger.info("Basket Repository has loaded XML File");
////        } catch (IOException e) {
////            logger.error("Basket Repository was not able to load CSV File");
////        }
//
//        xmlRecords.stream()
//                .map(xmlMapper::xmlRecordToBasket)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .forEach(basket -> baskets.put(basket.getCustomerNumber(), basket));
    }
}
