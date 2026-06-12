package com.zyrovia_store.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//		Map<String, Object> error = new HashMap<>();
//
//		error.put("timestamp : ", LocalDateTime.now());
//		error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
//		error.put("error", "Internal Server error");
//		error.put("message", ex.getMessage());
//     
//      return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

		ErrorResponseDto dto = mapToResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server error",
				ex.getMessage());

		return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handle ResourceNotFoundException Exception
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {

//		Map<String, Object> error = new HashMap<>();
//
//		error.put("timestamp : ", LocalDateTime.now());
//		error.put("status", HttpStatus.NOT_FOUND.value());
//		error.put("error", "Not Found");
//		error.put("message", ex.getMessage());
//
//		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

		ErrorResponseDto dto = mapToResponseError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());

		return new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
	}

}
