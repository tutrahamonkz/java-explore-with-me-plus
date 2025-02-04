package ru.practicum.category.exceptions;


public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
