package net.safedata.reactive.spring.rsocket.domain;

public record TicketRequest(int requestId, TicketStatus ticketStatus) {}