package net.safedata.reactive.spring.rsocket.web;

import net.safedata.reactive.spring.rsocket.domain.TicketRequest;
import net.safedata.reactive.spring.rsocket.domain.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

@RestController
public class RequestingController {

    private static final Random RANDOM = new Random(200);

    private final Mono<RSocketRequester> rSocketRequesterMono;

    @Autowired
    public RequestingController(Mono<RSocketRequester> rSocketRequesterMono) {
        this.rSocketRequesterMono = rSocketRequesterMono;
    }

    @GetMapping("/fire")
    public Mono<String> fireAndForget() {
        return rSocketRequesterMono.flatMap(requester -> requester.route("ticket.cancel")
                                                                  .data(new TicketRequest(RANDOM.nextInt(200), TicketStatus.TICKET_ISSUED))
                                                                  .retrieveMono(String.class));
    }
}
