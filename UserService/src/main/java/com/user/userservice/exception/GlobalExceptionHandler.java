package com.user.userservice.exception;

import com.user.userservice.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CuisineIdNotFoundException.class)
    public ResponseEntity<ApiResponse> handleCuisineIdNotFoundException(CuisineIdNotFoundException ex) {
        ApiResponse response = ApiResponse.builder()
                .response(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(DuplicateCuisineException.class)
    public ResponseEntity<ApiResponse> handleDuplicateCuisineException(DuplicateCuisineException ex) {
        ApiResponse response = ApiResponse.builder()
                .response(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorResponse response = ErrorResponse.builder().error(ex.getMessage()).timestamp(LocalDateTime.now()).statusMessage(HttpStatus.NOT_FOUND.toString()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(IncorrectPasswordException ex) {
        ErrorResponse response = ErrorResponse.builder().error(ex.getMessage()).timestamp(LocalDateTime.now()).statusMessage(HttpStatus.BAD_REQUEST.toString()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder().error(e.getMessage()).timestamp(LocalDateTime.now()).statusMessage(HttpStatus.UNAUTHORIZED.toString()).build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponse response = ErrorResponse.builder().error(e.getMessage()).timestamp(LocalDateTime.now()).statusMessage(HttpStatus.FORBIDDEN.toString()).build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ApiResponse> userIdNotFoundException(UserIdNotFoundException ex){
        ApiResponse response=ApiResponse.builder()
                .response(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(CountryAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCountryAlreadyExistsException(CountryAlreadyExistsException exception)
    {
        return new ResponseEntity<>
                (
                        ApiResponse.builder()
                                .response(exception.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                        ,
                        HttpStatus.IM_USED
                );
    }
    @ExceptionHandler(CountryIdNotFoundException.class)
    public ResponseEntity<ApiResponse> handleCountryIdNotExistsException(CountryIdNotFoundException exception)
    {
        return new ResponseEntity<>
                (
                        ApiResponse.builder()
                                .response(exception.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                        ,
                        HttpStatus.NOT_FOUND
                );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            errorMessage.append(" ").append(message).append(". ");
        });
        ApiResponse response = ApiResponse.builder()
                .response(errorMessage.toString().trim())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse> constraintViolationException(SQLIntegrityConstraintViolationException ex) {
        ApiResponse response=ApiResponse.builder()
                .response(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> passwordMismatchException(PasswordMismatchException ex){
        ApiResponse response=ApiResponse.builder()
                .response(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public  ResponseEntity<ApiResponse> emailNotFoundException(EmailNotFoundException ex){
        ApiResponse response=ApiResponse.builder()
                .response(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }


    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .statusMessage(HttpStatus.BAD_REQUEST.toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}