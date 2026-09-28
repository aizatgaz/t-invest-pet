package org.invest.repository;

import lombok.RequiredArgsConstructor;
import org.invest.dto.db.Account;
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
public class JdbcAccountRepository implements Repository<Account> {

    private static final RowMapper<Account> ACCOUNT_ROW_MAPPER =
            (resultSet, rowNumber) ->
                    new Account()
                            .setAccountId(resultSet.getObject("account_id", UUID.class))
                            .setName(resultSet.getString("name"))
                            .setStatus(resultSet.getString("status"))
                            .setTotalAmountRub(resultSet.getBigDecimal("total_amount_rub"))
                            .setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Account> findById(
            UUID id
    ) {
        String sql = """
                select * from accounts where account_id = ?;
                """;
        return jdbcTemplate.query(sql, Map.of("id", id), ACCOUNT_ROW_MAPPER)
                .stream()
                .findFirst();
    }

    @Override
    public List<Account> findAll() {
        String sql = """
                select * from accounts;
                """;
        return jdbcTemplate.query(sql, Map.of(), ACCOUNT_ROW_MAPPER);
    }

    public List<Account> findByStatus(String status) {
        String sql = """
                select * from accounts where status = ?;
                """;
        return jdbcTemplate.query(sql, Map.of("status", status), ACCOUNT_ROW_MAPPER);
    }

    @Override
    public Account save(
            Account account
    ) {
        String sql = """
                insert into accounts (:account_id, :name, :status, :total_amount_rub, :updated_at)
                values (?, ?, ?, ?, ?)
                """;
        var params = new MapSqlParameterSource()
                .addValue("account_id", account.getAccountId())
                .addValue("name", account.getName())
                .addValue("status", account.getStatus())
                .addValue("total_amount_rub", account.getTotalAmountRub())
                .addValue("updated_at", account.getUpdatedAt());
        return jdbcTemplate.queryForObject(sql, params, ACCOUNT_ROW_MAPPER);
    }

    @Override
    public void deleteById(
            UUID id
    ) {
        String sql = """
                delete from accounts where account_id = ?;
                """;
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
