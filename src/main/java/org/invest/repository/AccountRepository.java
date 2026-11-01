package org.invest.repository;

import lombok.RequiredArgsConstructor;
import org.invest.dto.db.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private static final RowMapper<Account> ACCOUNT_ROW_MAPPER =
            (rs, rowNum) -> new Account()
                    .setAccountId(rs.getObject("account_id", UUID.class))
                    .setName(rs.getString("name"))
                    .setStatus(rs.getString("status"))
                    .setTotalAmountRub(rs.getBigDecimal("total_amount_rub"))
                    .setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

    private final JdbcTemplate jdbcTemplate;

    public Account findById(
            UUID id
    ) {
        String sql = """
                select * from accounts where account_id = ?;
                """;
        return jdbcTemplate.queryForObject(sql, ACCOUNT_ROW_MAPPER, id);
    }

    public List<Account> findAll() {
        String sql = """
                select * from accounts;
                """;
        return jdbcTemplate.query(sql, ACCOUNT_ROW_MAPPER);
    }

    public List<Account> findByStatus(String status) {
        String sql = """
                select * from accounts where status = ?;
                """;
        return jdbcTemplate.query(sql, ACCOUNT_ROW_MAPPER, status);
    }

    public void save(
            Account account
    ) {
        String sql = """
                insert into accounts (account_id, name, status, total_amount_rub, updated_at)
                values (?, ?, ?, ?, ?);
                """;
        jdbcTemplate.update(sql,
                account.getAccountId(),
                account.getName(),
                account.getStatus(),
                account.getTotalAmountRub(),
                account.getUpdatedAt()
        );
    }

    public void deleteById(
            UUID id
    ) {
        String sql = """
                delete from accounts where account_id = ?;
                """;
        jdbcTemplate.update(sql, id);
    }
}
