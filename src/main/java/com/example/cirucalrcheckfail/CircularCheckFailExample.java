package com.example.cirucalrcheckfail;

import com.di.Container;

public class CircularCheckFailExample {
    public static void main(String[] args) {
        Container container = new Container();
        container.scanPackage(CircularCheckFailExample.class.getPackageName());
    }
}
