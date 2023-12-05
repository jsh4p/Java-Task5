package org.jshap;

import org.jshap.example.SomeBean;

public class Main {
    public static void main(String[] args) {
        ((SomeBean) (Injector.inject(new SomeBean(),"injector_example.properties"))).foo();
    }
}