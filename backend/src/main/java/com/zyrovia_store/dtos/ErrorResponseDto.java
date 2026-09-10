package com.zyrovia_store.dtos;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
		name = "Error Response",
		description = "Standard error response returned"
					  + " when an API request fails"
)
public class ErrorResponseDto {

	@Schema(
			description = "Date and time when the error occurred",
	        example = "2026-09-09T11:30:45"
	)
	private LocalDateTime timeStamp;
	
    @Schema(
            description = "HTTP status code",
            example = "400"
    )
	private Integer status;
    
    @Schema(
            description = "HTTP error name",
            example = "Bad Request"
    )
	private String error;
    
    @Schema(
            description = "Detailed error message",
            example = "Invalid status transition from PENDING to SHIPPED"
    )
	private String message;
}
