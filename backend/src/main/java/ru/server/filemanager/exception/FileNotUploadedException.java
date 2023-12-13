package ru.server.filemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FileNotUploadedException extends RuntimeException{
    public FileNotUploadedException(String message) {
        super(message);
    }
}
