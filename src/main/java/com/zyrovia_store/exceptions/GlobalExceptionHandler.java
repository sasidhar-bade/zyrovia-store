package com.zyrovia_store.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zyrovia_store.dtos.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Convert exception details into a standard ErrorResponseDto
	private ErrorResponseDto mapToResponseError(Integer status, String error, String message) {

		ErrorResponseDto dto = ErrorResponseDto.builder()
				.timeStamp(LocalDateTime.now())
				.status(status)
				.error(error)
				.message(message)
				.build();
		
		return dto;
	}

	// Handle unexpected or unhandled exceptions
	// Returns HTTP 500 Internal Server Error
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {

		ErrorResponseDto errorResponseDto = 
				this.mapToResponseError(
						HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Internal Server error",
						ex.getMessage());

		return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handle requested resources that do not exist
	// Returns HTTP 404 Not Found
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {

		ErrorResponseDto errorResponseDto = this.mapToResponseError(
				HttpStatus.NOT_FOUND.value(), 
				"Not Found", 
				ex.getMessage());

		return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
	}
	
	// Handle authenticated users who do not have sufficient permissions
	// Returns HTTP 403 Forbidden	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
			AccessDeniedException ex) {
		
		ErrorResponseDto errorResponseDto = this.mapToResponseError(
				HttpStatus.FORBIDDEN.value(),
				"Forbidden",
				ex.getMessage());
		
		return new ResponseEntity<>(errorResponseDto, HttpStatus.FORBIDDEN);
	}
	
	// Handle malformed or invalid JSON request bodies
	// Returns HTTP 400 Bad Request	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
			HttpMessageNotReadableException ex) {
		
		String message = "Invalid request body";
		
		// Check whether the request contains an unsupported JSON field
	    if (ex.getMessage() != null
	            && ex.getMessage().contains("Unrecognized property")) {

	        message = "Request contains an unsupported field";
	    }
		
		ErrorResponseDto errorResponseDto = this.mapToResponseError(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				message);
		
		return new ResponseEntity<>(errorResponseDto,HttpStatus.BAD_REQUEST);
	}
	
	// Handle Bean Validation errors from @Valid request DTOs
	// Returns HTTP 400 Bad Request with field-specific validation messages
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException e){
		
		Map<String, String> errors = new HashMap<>();
		
		e.getBindingResult()
			.getFieldErrors()
			.forEach(error -> errors.put(
					error.getField(),
					error.getDefaultMessage()));
		
		return ResponseEntity.badRequest().body(errors);
	}
	
	// Handle business validation errors such as
	// duplicate email, invalid quantity, insufficient stock, etc.
	// Returns HTTP 400 Bad Request 
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handeIllegalArgumentException(IllegalArgumentException e) {
		
		Map<String, String> errorResponse = new HashMap<>();

		errorResponse.put("error", e.getMessage());
		
		return ResponseEntity.badRequest().body(errorResponse);
	}
}