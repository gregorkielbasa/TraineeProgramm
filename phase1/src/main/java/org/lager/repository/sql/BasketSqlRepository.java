package org.lager.repository.sql;

import org.lager.exception.RepositoryException;
import org.lager.model.Basket;
import org.lager.repository.BasketRepository;
import org.lager.repository.sql.functionalInterface.CommandQuery;
import org.lager.repository.sql.functionalInterface.CommandUpdate;
import org.lager.repository.sql.functionalInterface.ResultSetDecoder;

import java.util.ArrayList;
import java.util.Optional;

public class BasketSqlRepository implements BasketRepository {
    private final BasketSqlMapper mapper;
    private final SqlConnector<Basket> connector;

    public BasketSqlRepository(BasketSqlMapper mapper, SqlConnector<Basket> connector) {
        this.mapper = mapper;
        this.connector = connector;

        initialTables();
    }

    private void initialTables() {
        CommandUpdate command = mapper.getInitialCommand();

        connector.sendToDB(command);
    }


    @Override
    public void save(Basket basket) throws RepositoryException {
        validateBasket(basket);
        ArrayList<CommandUpdate> commandQueue = new ArrayList<>();
        commandQueue.add(mapper.getDeleteWholeBasketCommand(basket.getCustomerId()));
        commandQueue.addAll(mapper.getInsertWholeBasketList(basket));
        connector.sendToDB(commandQueue.toArray(new CommandUpdate[0]));
    }

    @Override
    public Optional<Basket> read(Long id) {
        validateId(id);
        CommandQuery command = mapper.getReadWholeBasketCommand(id);
        ResultSetDecoder<Optional<Basket>> decoder = mapper.getResultSetDecoder();

        return connector.receiveFromDB(command, decoder);
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        validateId(id);
        CommandUpdate command = mapper.getDeleteWholeBasketCommand(id);

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
