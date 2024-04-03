package org.lager.service;

public class BasketService {

//    private final ProductService productService;
//    private final CustomerService customerService;
//    private final BasketXmlEditor xmlEditor;
//    private final Map<Long, Basket> baskets;
//    private final Logger logger = LoggerFactory.getLogger(BasketService.class);
//
//    public BasketService(CustomerService customerService, ProductService productService, BasketXmlEditor xmlEditor) {
//        this.customerService = customerService;
//        this.productService = productService;
//        this.xmlEditor = xmlEditor;
//        this.baskets = new HashMap<>();
//        loadFromFile();
//    }
//
//    private Optional<Basket> getBasket(long customerNumber) {
//        return Optional.ofNullable(baskets.get(customerNumber));
//    }
//
//    public void emptyBasket(long customerNumber) {
//        logger.info("BasketService empties {} Basket", customerNumber);
//        baskets.remove(customerNumber);
//        saveToFile();
//    }
//
//    public void removeFromBasket(long customerNumber, long productNumber) {
//        logger.debug("BasketService remove {} Product from {} Basket", productNumber, customerNumber);
//        getBasket(customerNumber)
//                .ifPresent((basket) -> basket.remove(productNumber));
//        saveToFile();
//    }
//
//    public Map<Long, Integer> getContentOfBasket(long customerNumber) {
//        return getBasket(customerNumber)
//                .map(Basket::getContent)
//                .orElse(Collections.emptyMap());
//    }
//
//    public void addToBasket(long customerNumber, long productNumber, int amount) {
//        logger.debug("BasketService starts to add {} Product to {} Basket", productNumber, customerNumber);
////        productService.validatePresence(productNumber);
//        Basket basket = getBasket(customerNumber)
//                .orElseGet(() -> createBasket(customerNumber));
//        basket.insert(productNumber, amount);
//        saveToFile();
//        logger.debug("BasketService finished to add {} Product to {} Basket", productNumber, customerNumber);
//    }
//
//    private Basket createBasket(long customerNumber) {
//        customerService.validatePresence(customerNumber);
//        Basket newBasket = new Basket(customerNumber);
//        baskets.put(customerNumber, newBasket);
//        saveToFile();
//        logger.info("BasketService created new Basket with ID {}", customerNumber);
//        return newBasket;
//    }
//
//    private void saveToFile() {
//        try {
//            xmlEditor.saveToFile(baskets.values().stream().toList());
//            logger.info("BasketService saved its state to XML File");
//        } catch (IOException e) {
//            logger.error("BasketService was not able to save XML File");
//        }
//    }
//
//    private void loadFromFile() {
//        try {
//            baskets.clear();
//            xmlEditor.loadFromFile().forEach(basket -> baskets.put(basket.getCustomerNumber(), basket));
//            logger.info("BasketService loaded its state from XML File");
//        } catch (IOException e) {
//            logger.warn("BasketService was not able to load XML File");
//        }
//    }
}