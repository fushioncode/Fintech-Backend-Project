package com.fintech.bepc.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fintech.bepc.model.dtos.APIError;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static HashMap<String, Object> errorBodyBuilder(
            String message,
            Object data) {
        HashMap<String, Object> errorBody = new HashMap<>();

        errorBody.put("success", false);
        errorBody.put("message", message);
        errorBody.put("data", data);

        return errorBody;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());
        return errorBodyBuilder(ex.getMessage(), errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Log the exception (if needed)
        return errorBodyBuilder(ex.getMessage(), null);
    }

    @ExceptionHandler(DatabaseException.class)
    public Map<String, Object> handleDatabaseException(DatabaseException ex) {
        // Return user-friendly message
        return errorBodyBuilder(ex.getMessage(), null);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Map<String, Object> handleNullPointerException(NullPointerException ex) {
        ex.printStackTrace();
        return errorBodyBuilder("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(NullPointerException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public Map<String, Object> handleException(Exception ex) {
        return errorBodyBuilder(
                "Oops! Something happened while processing request. Please check your request", null);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({ExpiredJwtException.class})
    public Map<String, Object> handleExpiredJwtException(ExpiredJwtException ex) {
        return errorBodyBuilder(ex.getMessage(), null);
    }

    private Map<String, Object> processFieldErrors(List<ObjectError> fieldErrors, Map<String, String> responseBody) {

        if (fieldErrors != null) {
            fieldErrors.forEach(error -> {
                String fieldName = error.getCode();
                String errorMessage = error.getDefaultMessage();
                responseBody.put(fieldName, errorMessage);
            });
            return errorBodyBuilder("Bad request", responseBody);
        }

        return null;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public Map<String, Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return errorBodyBuilder(
                ex.getMessage(),
                null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingRequestHeaderException.class})
    public Map<String, Object> handleCardFusionException(MissingRequestHeaderException ex) {
        return Map.of("responseCode", "400", "responseMessage",
                Objects.requireNonNull(ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedException.class})
    public Map<String, Object> handleUnauthorizedException(UnauthorizedException ex) {
        return errorBodyBuilder(
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIError> handleApiException(APIException ex, HttpServletRequest request) {
        log.error("APIException: {}", ex.getMessage(), ex);
        APIError apiError = APIError.builder()
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .statusCode(ex.getStatusCode())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(ex.getStatusCode()).body(apiError);
    }
}

