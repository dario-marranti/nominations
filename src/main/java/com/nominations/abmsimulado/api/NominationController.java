package com.nominations.abmsimulado.api;

import com.nominations.abmsimulado.service.NominationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;

@RestController
@RequestMapping("/v1/nominations")
@Tag(name = "ABM SIMULADO", description = "Nominaciones con procesamiento asincrono simulado")
public class NominationController {

    private final NominationService service;

    public NominationController(NominationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Alta de nominacion",
            description = "Persiste la nominacion en H2 y devuelve un uuid para consultar el estado.")
    @ApiResponse(responseCode = "202", description = "Aceptada, devuelve uuid",
            content = @Content(schema = @Schema(implementation = NominationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Request invalido",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<NominationResponse> create(@Valid @RequestBody NominationRequest request) {
        String uuid = service.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{uuid}").buildAndExpand(uuid).toUri();
        return ResponseEntity.accepted().location(location).body(NominationResponse.accepted(uuid));
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Consulta de estado",
            description = "La primera consulta sortea una demora de 0 a 30 s. Antes de cumplirse responde 200 "
                    + "sin novedades; despues responde 201 con el id de base de datos.")
    @ApiResponse(responseCode = "200", description = "Sin novedades (PENDING)",
            content = @Content(schema = @Schema(implementation = NominationResponse.class)))
    @ApiResponse(responseCode = "201", description = "Creado (CREATED) con id de base de datos",
            content = @Content(schema = @Schema(implementation = NominationResponse.class)))
    @ApiResponse(responseCode = "404", description = "uuid inexistente",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<?> get(@Parameter(description = "uuid devuelto por el POST") @PathVariable String uuid) {
        return service.poll(uuid)
                .<ResponseEntity<?>>map(n -> n.isReady(Instant.now())
                        ? ResponseEntity.status(HttpStatus.CREATED).body(NominationResponse.created(n.getUuid(), n.getId()))
                        : ResponseEntity.ok(NominationResponse.pending(n.getUuid())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Nominacion no encontrada: " + uuid)));
    }
}
