package dev.jordanjoseph.backend.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** helper method to ensure a consistent JSON error response format
     * timestamp: when the error happened
     * status: HTTP status codes
     * error: HTTP status reason phrase (e.g. Forbidden, Not Found)
     * message: the actual cause, error message (e.g. Account doesn't exist)
     * path: the uri that triggered the error
     * */
    private Map<String, Object> body(HttpStatus status, String message, HttpServletRequest request) {
        return Map.of(
                "timestamp",  Instant.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "path", request.getRequestURI()
        );
    }

    /** catches AccessDeniedExceptions thrown, returns HTTP 403 Forbidden with the JSON body
     * the error is triggered when an authenticated user tries to access resources
     * that they don't have access to (e.g. Admin only, Account not yours)
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        return body(HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    /** catches IllegalArgumentExceptions thrown, returns HTTP 400 Bad Request with the JSON body
     * trigged when a method has been invoked with inappropriate arguments (e.g. Insufficient funds)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        return body(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    /** catches MethodArgumentNotValidExceptions thrown, returns HTTP 400 Bad Requests with the JSON body
     * the error is triggered when @Valid annotations fail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleFailedValidation(MethodArgumentNotValidException e, HttpServletRequest request) {

        // get the results of binding and validation, get all field errors,
        // find the first field that cause an error and create a message string form it, otherwise use default message
        // binding is when the data is converted from HTTP request body or params into a java object
        // validation is when the data respects/adheres to the defined requirement
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst().map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .orElse("Validation Error");

        return body(HttpStatus.BAD_REQUEST, message, request);
    }
    
    /** catches all exceptions that are not explicitly handled */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleOther(Exception e, HttpServletRequest request) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request);
    }





}
