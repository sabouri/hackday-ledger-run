package com.kindredgroup.hackday.gcs.ledger.bank;

import com.kindredgroup.hackday.gcs.ledger.Bank;
import com.kindredgroup.hackday.gcs.ledger.LedgerApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BankImplTest extends LedgerApplicationTests {
    @Autowired
    private Bank bank;

    @Test
    public void getBalanceCreatesAccount() {
        String qualifiedUsername = UUID.randomUUID().toString();
        BigDecimal balance = bank.balance(qualifiedUsername);
        assertNotNull(balance);
    }

    @Test
    public void twoGetBalancesAreOK() {
        String qualifiedUsername = UUID.randomUUID().toString();

        BigDecimal balance = bank.balance(qualifiedUsername);
        assertNotNull(balance);

        BigDecimal balanceAgain = bank.balance(qualifiedUsername);
        assertNotNull(balanceAgain);
    }

    @Test
    public void depositCreatesAccount() {
        String qualifiedUsername = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.TEN;

        BigDecimal newBalance = bank.deposit(qualifiedUsername, amount);
        assertNotNull(newBalance);
    }

    @Test
    public void depositAddsMoney() {
        String qualifiedUsername = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.TEN;

        BigDecimal oldBalance = bank.balance(qualifiedUsername);
        BigDecimal newBalance = bank.deposit(qualifiedUsername, amount);

        assertEquals(oldBalance.add(amount), newBalance);
    }

    @Test
    public void withdrawCreatesAccount() {
        String qualifiedUsername = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.TEN;

        BigDecimal newBalance = bank.withdraw(qualifiedUsername, amount);
        assertNotNull(newBalance);
    }

    @Test
    public void withdrawSubtractsMoney() {
        String qualifiedUsername = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.TEN;

        BigDecimal oldBalance = bank.balance(qualifiedUsername);
        BigDecimal newBalance = bank.withdraw(qualifiedUsername, amount);

        assertEquals(oldBalance.subtract(amount), newBalance);
    }

    @Test(expected = RuntimeException.class)
    public void bigWithdrawIsRejected() {
        String qualifiedUsername = UUID.randomUUID().toString();
        BigDecimal balance = bank.balance(qualifiedUsername);
        BigDecimal amount = balance.multiply(BigDecimal.TEN);
        bank.withdraw(qualifiedUsername, amount);
    }
}
