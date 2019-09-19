package com.kindredgroup.hackday.gcs.ledger;

import java.math.BigDecimal;
import java.util.Currency;

public interface Bank {
    Currency SYSTEM_CURRENCY = Currency.getInstance("IRR");

    BigDecimal balance(String qualifiedUsername);
    BigDecimal deposit(String qualifiedUsername, BigDecimal amount);
    BigDecimal withdraw(String qualifiedUsername, BigDecimal amount);
}
