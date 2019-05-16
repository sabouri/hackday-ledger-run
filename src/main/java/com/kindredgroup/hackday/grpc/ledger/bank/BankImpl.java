package com.kindredgroup.hackday.grpc.ledger.bank;

import com.kindredgroup.hackday.grpc.ledger.Bank;
import com.kindredgroup.hackday.grpc.ledger.bank.model.AccountEntity;
import com.kindredgroup.hackday.grpc.ledger.bank.model.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Optional;

@Component
public class BankImpl implements Bank {
    private AccountRepository accountRepository;
    private TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BankImpl(AccountRepository accountRepository, TransactionTemplate transactionTemplate) {
        this.accountRepository = accountRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public BigDecimal balance(String qualifiedUsername) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findById(qualifiedUsername);
        if (accountEntityOptional.isPresent()) {
            return accountEntityOptional.get().getBalance();
        }

        tryCreatingAccount(qualifiedUsername);
        return accountRepository.findById(qualifiedUsername).orElseThrow().getBalance();
    }

    @Override
    public BigDecimal deposit(String qualifiedUsername, BigDecimal amount) {
        BigDecimal balance = doTransaction(qualifiedUsername, amount);
        if (balance != null) {
            return balance;
        }

        tryCreatingAccount(qualifiedUsername);

        return doTransaction(qualifiedUsername, amount);
    }

    @Override
    public BigDecimal withdraw(String qualifiedUsername, BigDecimal amount) {
        amount = amount.negate();

        BigDecimal balance = doTransaction(qualifiedUsername, amount);
        if (balance != null) {
            return balance;
        }

        tryCreatingAccount(qualifiedUsername);

        BigDecimal balanceAgain = doTransaction(qualifiedUsername, amount);
        if (balanceAgain == null) {
            throw new RuntimeException("Not enough money.");
        }
        return balanceAgain;
    }

    private BigDecimal doTransaction(String qualifiedUsername, BigDecimal amount) {
        int updatedRows = accountRepository.updateBalance(qualifiedUsername, amount);
        if (updatedRows == 0) {
            return null;
        }

        return balance(qualifiedUsername);
    }

    private void tryCreatingAccount(String qualifiedUsername) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setQualifiedUsername(qualifiedUsername);
        accountEntity.setBalance(BigDecimal.valueOf(1_000_000));

        transactionTemplate.execute(transactionStatus -> {
            entityManager.persist(accountEntity);
            return null;
        });
    }
}
