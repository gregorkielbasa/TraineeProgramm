package org.lager.repository.sql.functionalInterface;

import java.sql.ResultSet;

public interface ResultSetDecoder<R>  {

    R decode(ResultSet resultSet);
}
