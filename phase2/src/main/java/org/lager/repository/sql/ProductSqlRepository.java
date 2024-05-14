package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.exception.SqlConnectionException;
import org.lager.model.Product;
import org.lager.repository.ProductRepository;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("database")
public class ProductSqlRepository implements ProductRepository {

    private final static Logger logger = LoggerFactory.getLogger(ProductSqlRepository.class);
    private final ProductSqlMapper mapper;
    private final SqlConnector connector;

    public ProductSqlRepository(ProductSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        SqlProcedure command = mapper.getInitialCommand();

        try {
            connector.sendToDB(command);
            logger.info("ProductRepository initialised Product Table");
        } catch (SqlConnectionException e) {
            logger.error("ProductRepository could not initialise Product Table");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public long getNextAvailableId() {
        long defaultProductId = 100_000_000;
        SqlFunction command = mapper.getProductWithHighestIdCommand();
        SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();

        try {
            Optional<Product> topProduct = connector.receiveFromDB(command, decoder);
            logger.debug("ProductRepository received Product with highest ID");
            return topProduct
                    .map(product -> product.getId() + 1)
                    .orElse(defaultProductId);
        } catch (SqlConnectionException e) {
            logger.error("ProductRepository could not read Product with highest ID");
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Optional<Product> read(Long id) {
        validateId(id);
        SqlFunction command = mapper.getReadCommand(id);
        SqlDecoder<Optional<Product>> decoder = mapper.getResultSetDecoder();

        try {
            logger.debug("ProductRepository received Product with {} ID", id);
            return connector.receiveFromDB(command, decoder);
        } catch (SqlConnectionException e) {
            logger.error("ProductRepository could not read Product with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        SqlProcedure command = mapper.getDeleteCommand(id);

        try {
            connector.sendToDB(command);
            logger.info("ProductRepository deleted Product with {} ID", id);
        } catch (SqlConnectionException e) {
            logger.error("ProductRepository could not delete Product with {} ID", id);
            throw new RepositoryException(e.getMessage());
        }
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
        SqlProcedure command = mapper.getInsertCommand(product);

        try {
            connector.sendToDB(command);
            logger.info("ProductRepository inserted Product with {} ID", product.getId());
        } catch (SqlConnectionException e) {
            logger.error("ProductRepository failed to insert Product with {} ID", product.getId());
            throw new RepositoryException(e.getMessage());
        }
    }

    private void updateName(Product product) throws RepositoryException {
        SqlProcedure command = mapper.getUpdateNameCommand(product);

        try {
            connector.sendToDB(command);
            logger.info("ProductRepository updated Product with {} ID", product.getId());
        } catch (SqlConnectionException e) {
            logger.info("ProductRepository failed to update Product with {} ID", product.getId());
            throw new RepositoryException(e.getMessage());
        }
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
