package com.nominations.abmsimulado.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Alta de nominacion")
public record NominationRequest(
        @JsonProperty("customer_id") @NotBlank
        @Schema(example = "123456") String customerId,

        @JsonProperty("account_id") @NotBlank
        @Schema(example = "987654") String accountId,

        @JsonProperty("card_id") @NotBlank
        @Schema(example = "TOKEN_OR_MASKED_REFERENCE") String cardId,

        @JsonProperty("alias") @NotBlank
        @Schema(example = "CUENTA_PRINCIPAL") String alias,

        @JsonProperty("request_id") @NotBlank
        @Schema(description = "Clave de idempotencia: si se repite, se devuelve el mismo uuid",
                example = "8fcb3fa0-7f19-4fc2-a803-cc76ac815001") String requestId
) {}
