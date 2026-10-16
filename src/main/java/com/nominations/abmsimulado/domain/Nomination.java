package com.nominations.abmsimulado.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "nominations")
public class Nomination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "card_id", nullable = false)
    private String cardId;

    @Column(nullable = false)
    private String alias;

    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /** Demora sorteada en el primer GET (null hasta entonces). */
    @Column(name = "delay_seconds")
    private Integer delaySeconds;

    /** Momento a partir del cual la nominacion se considera creada. */
    @Column(name = "ready_at")
    private Instant readyAt;

    public boolean isReady(Instant now) {
        return readyAt != null && !now.isBefore(readyAt);
    }

    public Long getId() { return id; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Integer getDelaySeconds() { return delaySeconds; }
    public void setDelaySeconds(Integer delaySeconds) { this.delaySeconds = delaySeconds; }
    public Instant getReadyAt() { return readyAt; }
    public void setReadyAt(Instant readyAt) { this.readyAt = readyAt; }
}
