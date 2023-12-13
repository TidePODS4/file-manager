package ru.server.filemanager.exception;

public class FileNotUpdatedException extends RuntimeException {
    public FileNotUpdatedException(String message) {
        super(message);
    }
}
