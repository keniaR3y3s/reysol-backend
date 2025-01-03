package com.revoktek.reysol.core.exceptions;


public class MapperParseObjectException extends RuntimeException  {

    private final String messageMapper;

    public MapperParseObjectException(String message) {
        super(message);
        this.messageMapper = message;
    }

    public MapperParseObjectException(Throwable cause) {
        super(cause);
        this.messageMapper = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return messageMapper;
    }
}
