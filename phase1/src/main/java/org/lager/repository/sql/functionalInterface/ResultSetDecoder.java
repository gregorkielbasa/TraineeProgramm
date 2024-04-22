package org.lager.repository.sql.functionalInterface;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetDecoder<R>  {

    R decode(ResultSet resultSet) throws SQLException;
}
