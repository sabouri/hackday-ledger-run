package com.kindredgroup.hackday.grpc.ledger;

import java.math.BigDecimal;

public interface Bank {
    BigDecimal balance(String qualifiedUsername);
    BigDecimal deposit(String qualifiedUsername, BigDecimal amount);
    BigDecimal withdraw(String qualifiedUsername, BigDecimal amount);

}
