package com.mykytapavlenko.blog;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static com.mykytapavlenko.blog.PaymentTransactionPatterns.PaymentTransaction;
import static com.mykytapavlenko.blog.OrderPatterns.Order;
import static javaslang.API.$;
import static javaslang.API.*;
import static javaslang.Predicates.isIn;

public class Demo {

    public static void main(String[] args) {
        // We want to display status to customer different from our internal
        // order.status == Completed && PaymentTransaction(notEmpty, Online) => Paid online
        // order.status == Completed && PaymentTransaction(notEmpty, Offline) => Paid offline

        Order order = new Order();
        order.setCode("code");
        order.setPaymentTransaction(new PaymentTransaction(BigDecimal.ONE, PaymentType.OFFLINE));
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotal(BigDecimal.ONE);

        String result = Match(order).of(
            Case(Order($(), $(OrderStatus.COMPLETED), PaymentTransaction($(eqTotal(order)), $(isIn(PaymentType.OFFLINE, PaymentType.ONLINE)))),
                    (o, s, p) -> "Paid" + p),
            Case(Order($(), $(OrderStatus.COMPLETED), PaymentTransaction($(eqTotal(order).negate()), $())), "Not fully paid"),
            Case(Order($(), $(), $()), "Not paid")
        );

        print(result);
    }

    private static Predicate<BigDecimal> eqTotal(Order order) {
        return (paidValue) -> paidValue.compareTo(order.getTotal()) == 0;
    }


}
