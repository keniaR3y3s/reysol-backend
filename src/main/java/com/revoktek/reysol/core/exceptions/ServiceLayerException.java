package com.revoktek.reysol.core.exceptions;

public class ServiceLayerException extends RuntimeException  {

    private final String message;

    public ServiceLayerException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceLayerException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

}
