package com.prodactivv.app.core.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResponseWrapper {

    private Throwable throwable;
    private String message;
    private HttpStatus httpStatus;

}
