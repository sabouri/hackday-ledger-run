package com.kindredgroup.hackday.grpc.ledger;

import com.kindredgroup.hackday.grpc.LedgererviceGrpc;
import com.unibet.wallet.infrastructure.protobuf.WalletClientProtos;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class LedgerServiceImpl extends LedgererviceGrpc.LedgererviceImplBase {
    @Override
    public void balance(WalletClientProtos.BalanceRequest request, StreamObserver<WalletClientProtos.BalanceResponse> responseObserver) {
        super.balance(request, responseObserver);
        request.getQualifiedUserName();
        //responseObserver.onNext(new Ba);
    }

    @Override
    public void deposit(WalletClientProtos.DepositRequest request, StreamObserver<WalletClientProtos.DepositResponse> responseObserver) {
        super.deposit(request, responseObserver);
        request.getAmount();

        //WalletClientProtos.DepositResponse.newBuilder().setAfterBalance()
        //responseObserver.onNext();
    }

    @Override
    public void withdraw(WalletClientProtos.WithdrawRequest request, StreamObserver<WalletClientProtos.WithdrawResponse> responseObserver) {
        super.withdraw(request, responseObserver);
    }
}
