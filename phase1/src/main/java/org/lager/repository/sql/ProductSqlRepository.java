package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Product;
import org.lager.repository.ProductRepository;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;

import java.util.Optional;

public class ProductSqlRepository implements ProductRepository {

    private final ProductSqlMapper mapper;
    private final SqlConnector<Product> connector;

    public ProductSqlRepository(ProductSqlMapper mapper, SqlConnector<Product> connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        CommandUpdate command = mapper.getInitialCommand();

        connector.sendToDB(command);
    }

    @Override
    public long getNextAvailableId() {
        long defaultProductId = 100_000_000;
        CommandQuery command = mapper.getProductWithHighestIdCommand();
        ResultSetDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();

        Optional<Product> topProduct = connector.receiveFromDB(command, decoder);

        return topProduct
                .map(product -> product.getId() + 1)
                .orElse(defaultProductId);
    }

    @Override
    public Optional<Product> read(Long id) {
        validateId(id);
        CommandQuery command = mapper.getReadCommand(id);
        ResultSetDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();

        return connector.receiveFromDB(command, decoder);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        CommandUpdate command = mapper.getDeleteCommand(id);

        if (read(id).isPresent())
            connector.sendToDB(command);
    }

    @Override
    public void save(Product product) throws RepositoryException {
        validateProduct(product);

        if (read(product.getId()).isPresent())
            updateName(product);
        else
            insert(product);
    }

    private void insert(Product product) throws RepositoryException {
        CommandUpdate command = mapper.getInsertCommand(product);

        connector.sendToDB(command);
    }

    private void updateName(Product product) throws RepositoryException {
        CommandUpdate command = mapper.getUpdateNameCommand(product);

        connector.sendToDB(command);
    }

    private void validateProduct(Product product) {
        if (product == null)
            throw new RepositoryException("Given Product is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given Product's ID is NULL");
    }
}
