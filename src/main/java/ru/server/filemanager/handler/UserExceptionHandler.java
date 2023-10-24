package ru.server.filemanager.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.server.filemanager.dto.ErrorResponse;
import ru.server.filemanager.exception.UserDoesNotExistException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    @ExceptionHandler(UserDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserDoesNotExistException(UserDoesNotExistException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
    }
}
