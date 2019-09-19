package com.kindredgroup.hackday.gcs.ledger.rest;

import com.kindredgroup.hackday.gcs.ledger.Bank;
import com.kindredgroup.hackday.gcs.ledger.exceptions.InsufficientFundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/ledger")
public class LedgerController {

    private final Bank bank;

    public LedgerController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping(value = "/deposit")
    public ResponseEntity deposit(@RequestParam String userName,
                                  @RequestParam BigDecimal amount) {
        BigDecimal balanceResponse = bank.deposit(userName, amount);
        return ResponseEntity.ok(balanceResponse);
    }

    @PostMapping(value = "/withdraw")
    public ResponseEntity withdraw(@RequestParam String userName,
                                   @RequestParam BigDecimal amount) {
        BigDecimal balanceResponse;
        try {
            balanceResponse = bank.withdraw(userName, amount);

        } catch (InsufficientFundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        }
        return ResponseEntity.ok(balanceResponse);
    }

    @GetMapping(value = "/getBalance")
    public ResponseEntity getBalance(@RequestParam String userName) {
        BigDecimal balanceResponse = bank.balance(userName);
        return ResponseEntity.ok(balanceResponse);
    }
}