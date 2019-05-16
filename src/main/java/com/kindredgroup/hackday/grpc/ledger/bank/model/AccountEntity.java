package com.kindredgroup.hackday.grpc.ledger.bank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class AccountEntity {
    @Id
    private String qualifiedUsername;

    @Column
    private BigDecimal balance;

    public String getQualifiedUsername() {
        return qualifiedUsername;
    }

    public void setQualifiedUsername(String qualifiedUsername) {
        this.qualifiedUsername = qualifiedUsername;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
