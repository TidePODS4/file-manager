package ru.server.filemanager.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.server.filemanager.dto.response.ErrorResponse;
import ru.server.filemanager.exception.FileNotDeletedException;
import ru.server.filemanager.exception.FileNotFoundException;
import ru.server.filemanager.exception.FileNotUploadedException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFileNotFoundException(FileNotFoundException ex){
        return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(FileNotUploadedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileNotUploadedException(FileNotUploadedException ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

    @ExceptionHandler(FileNotDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFileNotDeletedExceptionException(FileNotDeletedException ex){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }
}
