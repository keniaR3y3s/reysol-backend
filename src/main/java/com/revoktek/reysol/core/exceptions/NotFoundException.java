package com.revoktek.reysol.core.exceptions;

public class NotFoundException extends RuntimeException  {

    private final String message;

    public NotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public NotFoundException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

}
