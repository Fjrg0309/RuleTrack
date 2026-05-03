package com.example.ruletrack.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Inválido"
                ));
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación", fieldErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotReadable(HttpMessageNotReadableException ex) {
        String msg = "El formato del cuerpo de la petición no es válido";
        if (ex.getMessage() != null && ex.getMessage().contains("LocalDate")) {
            msg = "El formato de la fecha no es válido. Usa el formato YYYY-MM-DD";
        }
        return buildResponse(HttpStatus.BAD_REQUEST, msg, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        String msg = "Ya existe un registro con esos datos";
        String cause = ex.getMostSpecificCause().getMessage();
        if (cause != null) {
            String causeLower = cause.toLowerCase();
            if (causeLower.contains("email")) msg = "El email ya está registrado";
            else if (causeLower.contains("username")) msg = "El nombre de usuario ya está en uso";
            else if (causeLower.contains("dni")) msg = "El DNI ya está registrado";
            else if (causeLower.contains("not-null") || causeLower.contains("not null")) {
                msg = "Faltan datos obligatorios. Comprueba que todos los campos están completos.";
            }
        }
        return buildResponse(HttpStatus.CONFLICT, msg, null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(java.util.stream.Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage(),
                        (a, b) -> a
                ));
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación", errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message,
                                                            Map<String, String> fieldErrors) {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
