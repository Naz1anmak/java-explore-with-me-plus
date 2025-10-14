package ru.practicum.exception;

public class EventErrorException extends RuntimeException {
    public EventErrorException(String message) {
        super(message);
    }
}
