package com.mykytapavlenko.pattern;

import java.util.function.Predicate;

import static javaslang.API.*;

public class EmployeeExample {

    public static void main(String[] args) {

        Employee employee = new Employee();
        employee.setAge(18);

        Match.Pattern0<Integer> age = $(18);

        System.out.println(Match(employee).of(Case(EmployeePatterns.Employee($(), $(), age), () -> "adult")));


        Predicate<Integer> isEven = i -> i % 2 == 0;
        Predicate<Integer> isOdd = isEven.negate();

        int i = 2;
        String s = Match(i).of(
                Case($(isOdd), "odd"),
                Case($(isEven), number -> String.valueOf(number) + " even"));

        System.out.println(s);


    }

}
