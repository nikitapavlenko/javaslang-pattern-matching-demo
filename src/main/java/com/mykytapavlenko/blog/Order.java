package com.mykytapavlenko.blog;

import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.match.annotation.Patterns;
import javaslang.match.annotation.Unapply;

import java.math.BigDecimal;

@Patterns
public class Order {

    private String code;
    private OrderStatus status;
    private PaymentTransaction paymentTransaction;
    private BigDecimal total;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }

    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }


    public BigDecimal getTotal() {
        return total;
    }

    @Unapply
    static Tuple3<String, OrderStatus, PaymentTransaction> Order(Order order) {
        return Tuple.of(order.code, order.status, order.paymentTransaction);
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
