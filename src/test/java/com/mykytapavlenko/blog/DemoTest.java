package com.mykytapavlenko.blog;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class DemoTest {

    private Demo demo = new Demo();
    private Order order = new Order();

    @Test
    public void shouldReturnPaidOffline_whenGetPaymentStatus_java(){
        order.setPaymentTransaction(new PaymentTransaction(BigDecimal.ONE, PaymentType.CASH));
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotal(BigDecimal.ONE);

        String paymentStatus_javaVersion = demo.getPaymentStatus_javaVersion(order);

        assertEquals(paymentStatus_javaVersion, "Paid offline with CASH");
    }

    @Test
    public void shouldReturnPaidOffline_whenGetPaymentStatus_javaslang(){
        order.setPaymentTransaction(new PaymentTransaction(BigDecimal.ONE, PaymentType.CASH));
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotal(BigDecimal.ONE);

        String paymentStatus_javaVersion = demo.getPaymentStatus_javaslangVersion(order);

        assertEquals(paymentStatus_javaVersion, "Paid offline with CASH");
    }

}