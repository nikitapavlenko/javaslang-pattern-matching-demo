package com.mykytapavlenko.blog;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.match.annotation.Patterns;
import javaslang.match.annotation.Unapply;

import java.math.BigDecimal;

@Patterns
public class PaymentTransaction {

    private final BigDecimal value;
    private final PaymentType paymentType;

    public PaymentTransaction(BigDecimal value, PaymentType paymentType) {
        this.value = value;
        this.paymentType = paymentType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public BigDecimal getValue() {
        return value;
    }


    @Unapply
    static Tuple2<BigDecimal, PaymentType> PaymentTransaction(PaymentTransaction paymentTransaction) {
        return Tuple.of(paymentTransaction.getValue(), paymentTransaction.getPaymentType());
    }
}
