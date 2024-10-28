package com.spaceships.exceptions;

public class SpaceshipNotFoundException extends RuntimeException {
    public SpaceshipNotFoundException(String message) {
        super(message);
    }
}