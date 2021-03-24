package com.prodactivv.app.core.exceptions;

import java.util.function.Supplier;

public class IllegalAccessException extends Exception implements Supplier<IllegalAccessException> {

    public IllegalAccessException(String msg) {
        super(msg);
    }

    @Override
    public IllegalAccessException get() {
        return this;
    }
}
