package org.lager.repository.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.lager.model.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record JsonOrder(long id,
                        long customerId,
                        LocalDateTime dateTime,
                        List<OrderItem> items) {
}
