package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Basket;
import org.lager.repository.BasketRepository;
import org.lager.repository.sql.functionalInterface.SqlFunction;
import org.lager.repository.sql.functionalInterface.SqlProcedure;
import org.lager.repository.sql.functionalInterface.SqlDecoder;

import java.util.ArrayList;
import java.util.Optional;

public class BasketSqlRepository implements BasketRepository {
    private final BasketSqlMapper mapper;
    private final SqlConnector connector;

    public BasketSqlRepository(BasketSqlMapper mapper, SqlConnector connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        SqlProcedure command = mapper.getInitialCommand();

        connector.sendToDB(command);
    }


    @Override
    public void save(Basket basket) throws RepositoryException {
        validateBasket(basket);
        ArrayList<SqlProcedure> commandQueue = new ArrayList<>();
        commandQueue.add(mapper.getDeleteWholeBasketCommand(basket.getCustomerId()));
        commandQueue.addAll(mapper.getInsertWholeBasketList(basket));
        connector.sendToDB(commandQueue.toArray(new SqlProcedure[0]));
    }

    @Override
    public Optional<Basket> read(Long id) {
        validateId(id);
        SqlFunction command = mapper.getReadWholeBasketCommand(id);
        SqlDecoder<Optional<Basket>> decoder = mapper.getResultSetDecoder();

        return connector.receiveFromDB(command, decoder);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        SqlProcedure command = mapper.getDeleteWholeBasketCommand(id);

        if (read(id).isPresent())
            connector.sendToDB(command);
    }

    private void validateBasket(Basket basket) {
        if (basket == null)
            throw new RepositoryException("Given Basket is NULL");
    }

    private void validateId(Long id) throws RepositoryException {
        if (id == null)
            throw new RepositoryException("Given Basket's ID is NULL");
    }
}
