package com.zyrovia_store.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zyrovia_store.dtos.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ErrorResponseDto mapToResponseError(Integer status, String error, String message) {

		ErrorResponseDto dto = ErrorResponseDto.builder().timeStamp(LocalDateTime.now()).status(status).error(error)
				.message(message).build();
		return dto;
	}

	// Handle Global Exception
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {

		ErrorResponseDto errorResponseDto = this.mapToResponseError(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server error",
				ex.getMessage());

		return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handle ResourceNotFoundException exception
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {

		ErrorResponseDto errorResponseDto = this.mapToResponseError(
				HttpStatus.NOT_FOUND.value(), 
				"Not Found", 
				ex.getMessage());

		return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
	}
	
	// Handle AccessDeniedException exception
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException ex) {
		
		ErrorResponseDto errorResponseDto = this.mapToResponseError(
				HttpStatus.FORBIDDEN.value(),
				"Forbidden",
				ex.getMessage());
		
		return new ResponseEntity<>(errorResponseDto, HttpStatus.FORBIDDEN);
	}
	
	// Handle invalid JSON request body
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		
		String message = "Invalid request body";

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
}
