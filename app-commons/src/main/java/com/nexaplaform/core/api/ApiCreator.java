package com.nexaplaform.core.api;


import com.nexaplaform.core.api.dto.MessageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(description = "Create a new entity")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = MessageResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = MessageResponseDTO.class)))
})
public @interface ApiCreator {

}
