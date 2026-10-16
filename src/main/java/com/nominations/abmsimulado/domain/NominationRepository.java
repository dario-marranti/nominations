package com.nominations.abmsimulado.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NominationRepository extends JpaRepository<Nomination, Long> {
    Optional<Nomination> findByUuid(String uuid);
    Optional<Nomination> findByRequestId(String requestId);
}
