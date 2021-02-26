package com.prodactivv.app.core.exceptions;

import java.util.function.Supplier;

public class InvitationExpiredException extends Exception implements Supplier<InvitationExpiredException> {

    public InvitationExpiredException() {
        super();
    }

    public InvitationExpiredException(String message) {
        super(message);
    }

    @Override
    public InvitationExpiredException get() {
        return this;
    }
}
