package com.kindredgroup.hackday.grpc.ledger.bank.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "UPDATE account SET balance = balance + :amount " +
                        "WHERE qualified_username = :qualifiedUsername AND balance + :amount >= 0"
    )
    int updateBalance(String qualifiedUsername, BigDecimal amount);
}
