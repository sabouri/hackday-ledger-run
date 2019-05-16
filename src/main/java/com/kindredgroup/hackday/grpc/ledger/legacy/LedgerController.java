package com.kindredgroup.hackday.grpc.ledger.legacy;

import com.google.protobuf.InvalidProtocolBufferException;
import com.kindredgroup.hackday.grpc.ledger.Bank;
import com.kindredgroup.hackday.grpc.ledger.LedgerServiceImpl;
import com.unibet.wallet.infrastructure.protobuf.WalletClientProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/ledger/wallet")
public class LedgerController extends BaseLedgerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LedgerController.class);

    private final Bank bank;

    public LedgerController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping("/getBalance")
    public ResponseEntity<byte[]> getBalance(HttpEntity<byte[]> requestEntity) throws InvalidProtocolBufferException {
        HttpHeaders headers = new HttpHeaders();
        try {
            final HttpHeaders httpHeaders = requestEntity.getHeaders();
            final List<String> values = httpHeaders.get(HTTP_X_REQUEST_ID);
            WalletClientProtos.BalanceRequest request = WalletClientProtos.BalanceRequest.parseFrom(requestEntity.getBody());
            initMDC(request, httpHeaders);

            String user = request.getQualifiedUserName();
            BigDecimal amount = bank.balance(user);

            WalletClientProtos.BalanceResponse.Builder balanceResponse = WalletClientProtos.BalanceResponse.newBuilder();
            balanceResponse.setCash(amount.toPlainString());
            balanceResponse.setCurrency(request.getCurrency());
            balanceResponse.setQualifiedUserName(user);
            WalletClientProtos.BalanceResponse response = balanceResponse.build();

            return getResponseEntity(headers, values, response.getClass().getSimpleName(), response.toByteArray());
        } catch (RuntimeException e) {
            LOGGER.error("Could not process wallet request", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<byte[]> deposit(HttpEntity<byte[]> requestEntity) throws InvalidProtocolBufferException {
        HttpHeaders headers = new HttpHeaders();
        try {
            final HttpHeaders httpHeaders = requestEntity.getHeaders();
            final List<String> values = httpHeaders.get(HTTP_X_REQUEST_ID);
            WalletClientProtos.DepositRequest request = WalletClientProtos.DepositRequest.parseFrom(requestEntity.getBody());

            initMDC(request, httpHeaders);

            WalletClientProtos.DepositResponse response = LedgerServiceImpl.deposit(request, bank);

            return getResponseEntity(headers, values, response.getClass().getSimpleName(), response.toByteArray());
        } catch (RuntimeException e) {
            LOGGER.error("Could not process wallet request", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            MDC.clear();
        }
    }

    private static ResponseEntity<byte[]> getResponseEntity(HttpHeaders headers, List<String> values, String simpleName, byte[] bytes) {
        headers.add(HTTP_X_PROTO_CLASS, simpleName);

        if (values != null && !values.isEmpty()) {
            headers.add(HTTP_X_REQUEST_ID, values.get(0));
        }

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/withdraw")
    public ResponseEntity<byte[]> withdraw(HttpEntity<byte[]> requestEntity) throws InvalidProtocolBufferException {
        HttpHeaders headers = new HttpHeaders();
        try {
            final HttpHeaders httpHeaders = requestEntity.getHeaders();
            final List<String> values = httpHeaders.get(HTTP_X_REQUEST_ID);
            WalletClientProtos.WithdrawRequest request = WalletClientProtos.WithdrawRequest.parseFrom(requestEntity.getBody());

            initMDC(request, httpHeaders);

            WalletClientProtos.WithdrawResponse response = LedgerServiceImpl.withdraw(request, bank);

            return getResponseEntity(headers, values, response.getClass().getSimpleName(), response.toByteArray());
        } catch (RuntimeException e) {
            LOGGER.error("Could not process wallet request", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            MDC.clear();
        }
    }
}