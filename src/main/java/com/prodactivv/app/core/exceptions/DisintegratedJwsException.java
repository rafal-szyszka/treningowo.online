package com.prodactivv.app.core.exceptions;

import java.util.function.Supplier;

public class DisintegratedJwsException extends Exception implements Supplier<DisintegratedJwsException> {

    public DisintegratedJwsException() {
        super();
    }

    public DisintegratedJwsException(String message) {
        super(message);
    }

    @Override
    public DisintegratedJwsException get() {
        return this;
    }
}
