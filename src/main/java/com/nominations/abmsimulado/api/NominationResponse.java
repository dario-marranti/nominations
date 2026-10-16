package com.nominations.abmsimulado.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estado de la nominacion")
public record NominationResponse(
        @JsonProperty("uuid")
        @Schema(example = "3f2a9c1e-5b7d-4e8a-9c0f-1a2b3c4d5e6f") String uuid,

        @JsonProperty("status")
        @Schema(allowableValues = {"PENDING", "CREATED"}, example = "PENDING") String status,

        @JsonProperty("message")
        @Schema(example = "Sin novedades") String message,

        @JsonProperty("id")
        @Schema(description = "Id en base de datos (solo cuando status = CREATED)", example = "1") Long id
) {
    public static NominationResponse accepted(String uuid) {
        return new NominationResponse(uuid, "PENDING", "Solicitud recibida", null);
    }

    public static NominationResponse pending(String uuid) {
        return new NominationResponse(uuid, "PENDING", "Sin novedades", null);
    }

    public static NominationResponse created(String uuid, Long id) {
        return new NominationResponse(uuid, "CREATED", "Creado", id);
    }
}
