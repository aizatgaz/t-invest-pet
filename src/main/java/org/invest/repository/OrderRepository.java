package org.invest.repository;

import lombok.RequiredArgsConstructor;
import org.invest.dto.db.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private static final RowMapper<Order> ACCOUNT_ROW_MAPPER =
            (rs, rowNum) -> new Order()
                    .setOrderId(rs.getObject("order_id", UUID.class))
                    .setAccountId(rs.getObject("account_id", UUID.class))
                    .setInstrumentId(rs.getObject("instrument_id", UUID.class))
                    .setDirection(rs.getString("direction"))
                    .setQuantity(rs.getLong("quantity"))
                    .setOrderType(rs.getString("order_type"))
                    .setPrice(rs.getBigDecimal("price"))
                    .setStatus(rs.getString("status"))
                    .setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

    private final JdbcTemplate jdbcTemplate;

    public Order findById(
            UUID id
    ) {
        String sql = """
                select * from orders where order_id = ?;
                """;
        return jdbcTemplate.queryForObject(sql, ACCOUNT_ROW_MAPPER, id);
    }

    public List<Order> findAll() {
        String sql = """
                select * from orders;
                """;
        return jdbcTemplate.query(sql, ACCOUNT_ROW_MAPPER);
    }

    public void save(
            Order order
    ) {
        String sql = """
                insert into orders (order_id, account_id, instrument_id, direction,
                quantity, order_type, price, status, updated_at)
                values (?, ?, ?, ?,
                ?, ?, ?, ?, ?);
                """;
        jdbcTemplate.update(sql,
                order.getOrderId(),
                order.getAccountId(),
                order.getInstrumentId(),
                order.getDirection(),
                order.getQuantity(),
                order.getOrderType(),
                order.getPrice(),
                order.getStatus(),
                order.getUpdatedAt()
        );
    }

    public void deleteById(
            UUID id
    ) {
        String sql = """
                delete from orders where order_id = ?;
                """;
        jdbcTemplate.update(sql, id);
    }
}
