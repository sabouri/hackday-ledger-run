package com.kindredgroup.hackday.grpc.ledger;

import com.kindredgroup.hackday.grpc.LedgerServiceGrpc;
import com.unibet.wallet.infrastructure.protobuf.WalletClientProtos;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@GrpcService
public class LedgerServiceImpl extends LedgerServiceGrpc.LedgerServiceImplBase {
    private final Bank bank;

    @Autowired
    public LedgerServiceImpl(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void balance(WalletClientProtos.BalanceRequest request, StreamObserver<WalletClientProtos.BalanceResponse> responseObserver) {
        String user = request.getQualifiedUserName();
        BigDecimal amount = bank.balance(user);

        WalletClientProtos.BalanceResponse.Builder balanceResponse = WalletClientProtos.BalanceResponse.newBuilder();
        balanceResponse.setCash(amount.toPlainString());
        balanceResponse.setCurrency(Bank.SYSTEM_CURRENCY.getCurrencyCode());
        balanceResponse.setQualifiedUserName(user);

        responseObserver.onNext(balanceResponse.build());
        responseObserver.onCompleted();
    }

    @Override
    public void deposit(WalletClientProtos.DepositRequest request, StreamObserver<WalletClientProtos.DepositResponse> responseObserver) {
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
        return WalletClientProtos.Balance.newBuilder().setCash(balanceAmount.toPlainString()).setCurrency(Bank.SYSTEM_CURRENCY.getCurrencyCode()).build();
    }

    public static WalletClientProtos.AmountDistribution createCashDistribution(BigDecimal amount) {
        return WalletClientProtos.AmountDistribution.newBuilder().setAmount(amount.toPlainString()).setMoneyType("Cash").setCurrency(Bank.SYSTEM_CURRENCY.getCurrencyCode()).build();
    }
}
