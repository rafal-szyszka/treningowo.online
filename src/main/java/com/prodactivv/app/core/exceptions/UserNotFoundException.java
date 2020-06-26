package com.prodactivv.app.core.exceptions;

import java.util.function.Supplier;

public class UserNotFoundException extends Exception implements Supplier<UserNotFoundException> {

    private static final String MESSAGE = "User of id: %s was not found!";

    public UserNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }

    @Override
    public UserNotFoundException get() {
        return this;
    }
}
