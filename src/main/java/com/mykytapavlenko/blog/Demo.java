package com.mykytapavlenko.blog;

import javaslang.control.Option;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.mykytapavlenko.blog.PaymentTransactionPatterns.PaymentTransaction;
import static com.mykytapavlenko.blog.OrderPatterns.Order;
import static javaslang.API.$;
import static javaslang.API.*;
import static javaslang.Predicates.isIn;
import static javaslang.Predicates.isNull;

public class Demo {

    public static void main(String[] args) {
        // We want to payment status to customer based on following rules:
        // when order.status == Completed && PaymentTransaction has value that equal to order's total and Offline or Online Payment Status
        // order.status == Completed && PaymentTransaction(notEmpty, Offline) => Paid offline

        Order order = new Order();
        order.setCode("code");
        order.setPaymentTransaction(new PaymentTransaction(BigDecimal.ONE, PaymentType.CASH));
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotal(BigDecimal.ONE);


        println(javaslangVersion(order));
        println(javaVersion(order));

    }

    private static Predicate<BigDecimal> eqTotal(Order order) {
        return (paidValue) -> paidValue.compareTo(order.getTotal()) == 0;
    }

     private static String javaVersion(Order order){
        if(order.getPaymentTransaction() == null){
            return "Not paid yet";
        }
        if(order.getStatus() == OrderStatus.CANCELLED && order.getPaymentTransaction().getPaymentType() == PaymentType.RETURN_AND_EXCHANGE){
            return "Payment cancelled";
        }
        if(order.getStatus() ==  OrderStatus.COMPLETED){
            List<PaymentType> onlineTypes = Arrays.asList(PaymentType.CREDIT_CARD, PaymentType.PAYPAL_EXPRESS, PaymentType.GIFT_CERTIFICATE);
            if(order.getTotal().compareTo(order.getPaymentTransaction().getValue()) == 0){
                PaymentType paymentType = order.getPaymentTransaction().getPaymentType();
                if(onlineTypes.contains(paymentType)){
                    return "Paid online with " + paymentType;
                } else if(paymentType == PaymentType.CASH){
                    return "Paid offline with CASH";
                }
            }
        }
        return null;
    }

     private static Option<String> javaslangVersion(Order order){
        return Match(order).option(
            Case(Order($(), $(OrderStatus.COMPLETED), PaymentTransaction($(eqTotal(order)), $(isIn(PaymentType.CREDIT_CARD, PaymentType.PAYPAL_EXPRESS, PaymentType.GIFT_CERTIFICATE)))),
                    (o, s, t) -> "Paid online with " + t.getPaymentType() ),
            Case(Order($(), $(OrderStatus.COMPLETED), PaymentTransaction($(eqTotal(order)), $(isIn(PaymentType.CASH)))), "Paid offline with CASH"),
            Case(Order($(), $(OrderStatus.CANCELLED), PaymentTransaction($(), $(PaymentType.RETURN_AND_EXCHANGE))), "Payment cancelled"),
            Case(Order($(), $(), $(isNull())), "Not paid yet")
        );
    }


}
