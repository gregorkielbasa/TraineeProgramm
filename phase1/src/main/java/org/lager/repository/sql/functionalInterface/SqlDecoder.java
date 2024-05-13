package org.lager.repository.sql.functionalInterface;

import java.sql.ResultSet;

public interface SqlDecoder<R>  {

    R decode(ResultSet resultSet);
}
