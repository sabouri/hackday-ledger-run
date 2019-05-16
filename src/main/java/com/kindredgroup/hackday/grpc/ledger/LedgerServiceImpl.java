package com.kindredgroup.hackday.grpc.ledger;

import com.kindredgroup.hackday.grpc.LedgererviceGrpc;
import com.unibet.wallet.infrastructure.protobuf.WalletClientProtos;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;

@GrpcService
public class LedgerServiceImpl extends LedgererviceGrpc.LedgererviceImplBase {
    private final Bank bank;
    private final static Currency bankCurrency = Currency.getInstance("SEK");

    @Autowired
    public LedgerServiceImpl(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void balance(WalletClientProtos.BalanceRequest request, StreamObserver<WalletClientProtos.BalanceResponse> responseObserver) {
        super.balance(request, responseObserver);

        String user = request.getQualifiedUserName();
        BigDecimal amount = bank.balance(user);

        WalletClientProtos.BalanceResponse.Builder balanceResponse = WalletClientProtos.BalanceResponse.newBuilder();
        balanceResponse.setCash(amount.toPlainString());
        balanceResponse.setCurrency(bankCurrency.getCurrencyCode());
        balanceResponse.setQualifiedUserName(user);

        responseObserver.onNext(balanceResponse.build());
        responseObserver.onCompleted();
    }

    @Override
    public void deposit(WalletClientProtos.DepositRequest request, StreamObserver<WalletClientProtos.DepositResponse> responseObserver) {
        super.deposit(request, responseObserver);

        WalletClientProtos.DepositResponse response = deposit(request, bank);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public static WalletClientProtos.DepositResponse deposit(WalletClientProtos.DepositRequest request, Bank bank) {
        String user = request.getQualifiedUserName();
        BigDecimal amount = new BigDecimal(request.getAmount());

        BigDecimal balanceAmount = bank.deposit(user, amount);

        WalletClientProtos.DepositResponse.Builder builder = WalletClientProtos.DepositResponse.newBuilder();
        builder.setAfterBalance(createCachBalance(balanceAmount));
        builder.setApproved(true);
        builder.addDistribution(createCashDistribution(amount));

        return builder.build();
    }

    @Override
    public void withdraw(WalletClientProtos.WithdrawRequest request, StreamObserver<WalletClientProtos.WithdrawResponse> responseObserver) {
        super.withdraw(request, responseObserver);

        WalletClientProtos.WithdrawResponse response = withdraw(request, bank);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public static WalletClientProtos.WithdrawResponse withdraw(WalletClientProtos.WithdrawRequest request, Bank bank) {
        String user = request.getQualifiedUserName();
        BigDecimal amount = new BigDecimal(request.getAmount());

        BigDecimal balanceAmount = bank.withdraw(user, amount);

        WalletClientProtos.WithdrawResponse.Builder builder = WalletClientProtos.WithdrawResponse.newBuilder();
        builder.setAfterBalance(createCachBalance(balanceAmount));
        builder.setApproved(true);
        builder.addDistribution(createCashDistribution(amount));

        return builder.build();
    }

    public static WalletClientProtos.Balance createCachBalance(BigDecimal balanceAmount) {
        return WalletClientProtos.Balance.newBuilder().setCash(balanceAmount.toPlainString()).setCurrency(bankCurrency.getCurrencyCode()).build();
    }

    public static WalletClientProtos.AmountDistribution createCashDistribution(BigDecimal amount) {
        return WalletClientProtos.AmountDistribution.newBuilder().setAmount(amount.toPlainString()).setMoneyType("C").setCurrency(bankCurrency.getCurrencyCode()).build();
    }
}
