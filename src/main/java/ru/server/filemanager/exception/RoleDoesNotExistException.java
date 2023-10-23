package ru.server.filemanager.exception;

public class RoleDoesNotExistException extends RuntimeException{
    public RoleDoesNotExistException(String message) {
        super(message);
    }
}
