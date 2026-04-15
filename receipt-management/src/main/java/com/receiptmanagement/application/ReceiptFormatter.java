package com.receiptmanagement.application;

import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.ReceiptDocument;

public interface ReceiptFormatter {

    ReceiptDocument format(PaymentDetails paymentDetails, CustomerInformation customerInformation);
}

