package com.nominations.abmsimulado.service;

import com.nominations.abmsimulado.api.NominationRequest;
import com.nominations.abmsimulado.domain.Nomination;
import com.nominations.abmsimulado.domain.NominationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class NominationService {

    private final NominationRepository repository;
    private final int maxDelaySeconds;

    public NominationService(NominationRepository repository,
                             @Value("${abm.simulado.max-delay-seconds:30}") int maxDelaySeconds) {
        this.repository = repository;
        this.maxDelaySeconds = maxDelaySeconds;
    }

    /** Persiste la nominacion y devuelve el uuid. Idempotente por request_id. */
    @Transactional
    public String create(NominationRequest req) {
        return repository.findByRequestId(req.requestId())
                .map(Nomination::getUuid)
                .orElseGet(() -> {
                    Nomination n = new Nomination();
                    n.setUuid(UUID.randomUUID().toString());
                    n.setCustomerId(req.customerId());
                    n.setAccountId(req.accountId());
                    n.setCardId(req.cardId());
                    n.setAlias(req.alias());
                    n.setRequestId(req.requestId());
                    n.setCreatedAt(Instant.now());
                    return repository.save(n).getUuid();
                });
    }

    /** Busca por uuid; en la primera consulta sortea la demora (0..max segundos). */
    @Transactional
    public Optional<Nomination> poll(String uuid) {
        return repository.findByUuid(uuid).map(n -> {
            if (n.getReadyAt() == null) {
                int delay = ThreadLocalRandom.current().nextInt(maxDelaySeconds + 1);
                n.setDelaySeconds(delay);
                n.setReadyAt(Instant.now().plusSeconds(delay));
            }
            return n;
        });
    }
}
