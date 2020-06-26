package com.prodactivv.app.admin.trainer.models.exceptions;

import java.util.function.Supplier;

public class ExerciseNotFoundException extends Exception implements Supplier<ExerciseNotFoundException> {

    private final static String MESSAGE = "Exercise of id: %s was not found!";

    public ExerciseNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }

    @Override
    public ExerciseNotFoundException get() {
        return this;
    }
}
