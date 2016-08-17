package tests;

import tests.model.Customer;

public class A {

    public static void main(String[] args) {

        getOne("kk");

    }

    static void getOne(String path, String ... parameters) {
        System.out.println(parameters);
    }

}

