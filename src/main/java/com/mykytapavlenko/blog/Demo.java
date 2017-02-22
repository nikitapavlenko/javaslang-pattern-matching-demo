package com.mykytapavlenko.blog;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.mykytapavlenko.blog.PaymentTransactionPatterns.PaymentTransaction;
import static com.mykytapavlenko.blog.OrderPatterns.Order;
import static javaslang.API.$;
import static javaslang.API.*;
import static javaslang.Predicates.isIn;
import static javaslang.Predicates.isNull;

public class Demo {

    public String getPaymentStatus_javaVersion(Order order) {
        if (order.getPaymentTransaction() == null) {
            return "Not paid yet";
        }
        if (order.getStatus() == OrderStatus.CANCELLED && order.getPaymentTransaction().getPaymentType() == PaymentType.RETURN) {
            return "Payment cancelled";
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            List<PaymentType> onlineTypes = Arrays.asList(PaymentType.CREDIT_CARD, PaymentType.PAYPAL, PaymentType.GIFT_CARD);
            if (order.getTotal().compareTo(order.getPaymentTransaction().getValue()) == 0) {
                PaymentType paymentType = order.getPaymentTransaction().getPaymentType();
                if (onlineTypes.contains(paymentType)) {
                    return "Paid online with " + paymentType;
                } else if (paymentType == PaymentType.CASH) {
                    return "Paid offline with CASH";
                }
            }
        }
        return "";
    }

    public String getPaymentStatus_javaslangVersion(Order order) {
        return Match(order).option(
            Case(Order($(), $(OrderStatus.COMPLETED), PaymentTransaction($(eqTotal(order)), $(isIn(PaymentType.CREDIT_CARD, PaymentType.PAYPAL, PaymentType.GIFT_CARD)))),
                    (o, s, t) -> "Paid online with " + t.getPaymentType()),
            Case(Order($(), $(OrderStatus.COMPLETED), PaymentTransaction($(eqTotal(order)), $(isIn(PaymentType.CASH)))), "Paid offline with CASH"),
            Case(Order($(), $(OrderStatus.CANCELLED), PaymentTransaction($(), $(PaymentType.RETURN))), "Payment cancelled"),
            Case(Order($(), $(), $(isNull())), "Not paid yet")).getOrElse("");
    }

    private static Predicate<BigDecimal> eqTotal(Order order) {
        return (paidValue) -> paidValue.compareTo(order.getTotal()) == 0;
    }

}
