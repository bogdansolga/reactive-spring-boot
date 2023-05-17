package net.safedata.reactive.spring.rsocket.web;

import net.safedata.reactive.spring.rsocket.domain.MovieScene;
import net.safedata.reactive.spring.rsocket.domain.TicketRequest;
import net.safedata.reactive.spring.rsocket.domain.TicketStatus;
import net.safedata.reactive.spring.rsocket.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Controller
public class MovieController {

    private static final Logger LOGGER = Loggers.getLogger(MovieController.class);

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @MessageMapping("ticket.cancel")
    public void cancelTicket(Mono<TicketRequest> request) {
        // cancel and refund asynchronously
        request.map(t -> new TicketRequest(t.requestId(), TicketStatus.TICKET_CANCELLED))
                      .doOnNext(t -> LOGGER.info("Cancelling the ticket with the ID '{}', the status is now '{}'", t.requestId(), t.ticketStatus()))
                      .subscribe();
    }

    @MessageMapping("ticket.purchase")
    public Mono<TicketRequest> purchaseTicket(Mono<TicketRequest> request){
        return request.doOnNext(t -> new TicketRequest(t.requestId(), TicketStatus.TICKET_ISSUED))
                      .doOnNext(t -> System.out.println("Purchase ticket: " + t.requestId() + " : " + t.ticketStatus()));

    }

    @MessageMapping("tv.movie")
    public Flux<MovieScene> playMovie(Flux<Integer> sceneIndex){
        return sceneIndex
                .map(index -> index - 1) // list is 0 based index
                .map(this.movieService::getScene)
                .delayElements(Duration.ofSeconds(1));
    }

    @MessageMapping("movie.stream")
    public Flux<MovieScene> playMovie(Mono<TicketRequest> request){
        return request.map(t -> t.ticketStatus().equals(TicketStatus.TICKET_ISSUED) ?
                              this.movieService.getScenes() : List.of())
                      .flatMapIterable(Function.identity())
                      .cast(MovieScene.class)
                      .delayElements(Duration.ofSeconds(1));
    }

}
