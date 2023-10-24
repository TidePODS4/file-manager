package ru.server.filemanager.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.server.filemanager.dto.ErrorResponse;
import ru.server.filemanager.exception.DirectoryAlreadyExistsException;
import ru.server.filemanager.exception.DirectoryNotCreatedException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DirectoryExceptionHandler {

    @ExceptionHandler(DirectoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDirectoryAlreadyExistsException(DirectoryAlreadyExistsException ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(DirectoryNotCreatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDirectoryNotCreatedException(DirectoryNotCreatedException ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }
}
