package ru.server.filemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DirectoryNotCreatedException extends RuntimeException{
    public DirectoryNotCreatedException(String message) {
        super(message);
    }
}
