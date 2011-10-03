package com.restbucks.ordering.activities;

public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(Exception ex) {
        super(ex);
    }

    public InvalidOrderException() {}

    private static final long serialVersionUID = 2300194325533639524L;

}
