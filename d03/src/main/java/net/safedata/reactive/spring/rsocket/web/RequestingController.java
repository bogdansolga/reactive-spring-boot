package net.safedata.reactive.spring.rsocket.web;

import net.safedata.reactive.spring.rsocket.domain.MovieScene;
import net.safedata.reactive.spring.rsocket.domain.Routes;
import net.safedata.reactive.spring.rsocket.domain.TicketRequest;
import net.safedata.reactive.spring.rsocket.domain.TicketStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
public class RequestingController {

    private static final Random RANDOM = new Random(200);

    private final Mono<RSocketRequester> rSocketRequesterMono;

    @Autowired
    public RequestingController(Mono<RSocketRequester> rSocketRequesterMono) {
        this.rSocketRequesterMono = rSocketRequesterMono;
    }

    @GetMapping("/fire")
    public Mono<Void> fireAndForget() {
        return rSocketRequesterMono.flatMap(requester -> requester.route(Routes.TICKET_CANCEL)
                                                                  .data(new TicketRequest(RANDOM.nextInt(200), TicketStatus.TICKET_ISSUED))
                                                                  .retrieveMono(Void.class));
    }

    @GetMapping("/request")
    public Mono<String> requestResponse() {
        return rSocketRequesterMono.flatMap(requester -> requester.route(Routes.TICKET_PURCHASE)
                                                                  .data(new TicketRequest(RANDOM.nextInt(200), TicketStatus.TICKET_ISSUED))
                                                                  .retrieveMono(String.class));
    }

    @GetMapping("/stream")
    public Flux<MovieScene> requestStream() {
        return rSocketRequesterMono.flatMapMany(requester -> requester.route(Routes.MOVIE_SCENES)
                                                                  .data(Flux.just(1, 2, 3))
                                                                  .retrieveFlux(MovieScene.class));
    }

    @GetMapping("/channel")
    public Flux<MovieScene> requestChannel() {
        return rSocketRequesterMono.flatMapMany(requester -> requester.route(Routes.MOVIE_STREAM)
                                                                      .data(new TicketRequest(RANDOM.nextInt(200), TicketStatus.TICKET_ISSUED))
                                                                      .retrieveFlux(MovieScene.class));
    }
}
