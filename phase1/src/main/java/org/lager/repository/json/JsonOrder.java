package org.lager.repository.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.lager.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record JsonOrder(long id,
                        long customerNumber,
                        List<OrderItem> items,
                        LocalDateTime dateTime) {
}
