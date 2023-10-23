package ru.server.filemanager.exception;

public class FileNotUploadedException extends RuntimeException{
    public FileNotUploadedException(String message) {
        super(message);
    }
}
