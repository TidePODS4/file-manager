package ru.server.filemanager.exception;

public class DirectoryNotUpdatedException extends RuntimeException{
    public DirectoryNotUpdatedException(String message) {
        super(message);
    }
}
