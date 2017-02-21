package com.mykytapavlenko.pattern;

import javaslang.Tuple;
import javaslang.Tuple3;
import javaslang.match.annotation.Patterns;
import javaslang.match.annotation.Unapply;

@Patterns
public class Employee {

    private String name;
    private String id;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Unapply
    public static Tuple3<String, String, Integer> Employee(Employee Employee) {
        return Tuple.of(Employee.getName(), Employee.getId(), Employee.getAge());
    }
}
