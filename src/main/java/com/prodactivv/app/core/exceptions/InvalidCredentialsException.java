package com.prodactivv.app.core.exceptions;

import java.util.function.Supplier;

public class InvalidCredentialsException extends Exception implements Supplier<InvalidCredentialsException> {

    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    @Override
    public InvalidCredentialsException get() {
        return this;
    }
}
