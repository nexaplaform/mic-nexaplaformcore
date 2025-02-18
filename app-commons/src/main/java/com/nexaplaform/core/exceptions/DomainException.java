package com.nexaplaform.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class DomainException extends RuntimeException {

    private final String code;
    private final List<String> messages;
    private final HttpStatus status;

    public DomainException(String code, List<String> messages, HttpStatus status) {
        this.code = code;
        this.messages = messages;
        this.status = status;
    }

    public DomainException(String message, String code, List<String> messages, HttpStatus status) {
        super(message);
        this.code = code;
        this.messages = messages;
        this.status = status;
    }
}
