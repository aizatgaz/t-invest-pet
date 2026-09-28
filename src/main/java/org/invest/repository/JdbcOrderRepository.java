package org.invest.repository;

import lombok.RequiredArgsConstructor;
import org.invest.dto.db.Order;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class JdbcOrderRepository implements Repository<Order> {

    private static final RowMapper<Order> ORDER_ROW_MAPPER =
            (resultSet, rowNumber) ->
                    new Order()
                            .setOrderId(resultSet.getObject("orderId", UUID.class))
                            .setAccountId(resultSet.getObject("account_id", UUID.class))
                            .setInstrumentId(resultSet.getObject("instrumentId", UUID.class))
                            .setDirection(resultSet.getString("direction"))
                            .setQuantity(resultSet.getLong("quantity"))
                            .setOrderType(resultSet.getString("orderType"))
                            .setPrice(resultSet.getBigDecimal("price"))
                            .setStatus(resultSet.getString("status"))
                            .setUpdatedAt(resultSet.getObject("updatedAt", LocalDateTime.class));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Order> findById(
            UUID id
    ) {
        String sql = """
                select * from orders where order_id = ?;
                """;
        return jdbcTemplate.query(sql, Map.of("id", id), ORDER_ROW_MAPPER)
                .stream()
                .findFirst();
    }

    @Override
    public List<Order> findAll() {
        String sql = """
                select * from orders;
                """;
        return jdbcTemplate.query(sql, Map.of(), ORDER_ROW_MAPPER);
    }

    @Override
    public Order save(
            Order order
    ) {
        String sql = """
                insert into orders (:order_id, :account_id, :instrument_id, :direction,
                :quantity, :order_type, :price, :status, :updated_at)
                values (?, ?, ?, ?, ?)
                """;
        var params = new MapSqlParameterSource()
                .addValue("orderId", order.getOrderId())
                .addValue("accountId", order.getAccountId())
                .addValue("instrumentId", order.getInstrumentId())
                .addValue("direction", order.getDirection())
                .addValue("quantity", order.getQuantity())
                .addValue("orderType", order.getOrderType())
                .addValue("price", order.getPrice())
                .addValue("status", order.getStatus())
                .addValue("updatedAt", order.getUpdatedAt());
        return jdbcTemplate.queryForObject(sql, params, ORDER_ROW_MAPPER);
    }


    @Override
    public void deleteById(
            UUID id
    ) {
        String sql = """
                delete from orders where order_id = ?;
                """;
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
