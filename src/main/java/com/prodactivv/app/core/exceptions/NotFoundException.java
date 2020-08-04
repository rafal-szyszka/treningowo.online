package com.prodactivv.app.core.exceptions;

import java.util.function.Supplier;

public class NotFoundException extends Exception implements Supplier<NotFoundException> {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public NotFoundException get() {
        return this;
    }
}
