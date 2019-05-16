package com.kindredgroup.hackday.grpc.ledger.legacy;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

import java.util.List;

public class BaseLedgerController {
    public static final String WALLET_CLIENT_HOST = "Client-Host";
    public static final String WALLET_CLIENT_APP = "Client-App";
    protected static final String HTTP_X_REQUEST_ID = "X-Request-ID";
    protected static final String HTTP_X_PROTO_CLASS = "X-Proto-Class";

    protected void initMDC(Message requestProto, HttpHeaders httpHeaders) {
        // Init MDC for logging
        MDC.put("userName", getUsernameFromRequest(requestProto));
        MDC.put("transId", getTransactionIdFromRequest(requestProto));
        MDC.put(WALLET_CLIENT_HOST, getClientHostFromRequest(httpHeaders));
        MDC.put(WALLET_CLIENT_APP, getClientAppFromRequest(httpHeaders));
    }

    protected String getUsernameFromRequest(final Message requestProto) {
        final Descriptors.FieldDescriptor qualifiedUserNameFieldDescriptor = requestProto.getDescriptorForType().findFieldByName("qualifiedUserName");
        if (null != qualifiedUserNameFieldDescriptor) {
            return (String)requestProto.getField(qualifiedUserNameFieldDescriptor);

        }
        return "unknown-user";
    }

    protected String getTransactionIdFromRequest(final Message requestProto) {
        final Descriptors.FieldDescriptor uniqueReferenceFieldDescriptor = requestProto.getDescriptorForType().findFieldByName("uniqueReference");

        if (null != uniqueReferenceFieldDescriptor) {
            return (String)requestProto.getField(uniqueReferenceFieldDescriptor);

        }
        return "unknown-transaction-id";
    }

    protected String getClientHostFromRequest(final HttpHeaders headers) {
        final List<String> values = headers.get(WALLET_CLIENT_HOST);

        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return "unknown-clientHost";
    }

    protected String getClientAppFromRequest(final HttpHeaders headers) {
        final List<String> values = headers.get(WALLET_CLIENT_APP);

        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }
        return "unknown-clientApp";
    }
}
