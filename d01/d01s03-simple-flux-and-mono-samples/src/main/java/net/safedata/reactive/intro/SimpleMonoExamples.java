package net.safedata.reactive.intro;

import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.time.LocalDateTime;

/**
 * A few examples for the usage of the {@link Mono} Reactor publisher
 */
public class SimpleMonoExamples {

    private static final Logger LOGGER = Loggers.getLogger(SimpleMonoExamples.class);

    public static void main(String[] args) {
        simpleMonoExamples();
    }

    private static void simpleMonoExamples() {
        Mono.just("Welcome to the Mono publisher!")
            .log(LOGGER)
            .subscribe(it -> System.out.println(Thread.currentThread().getName() + " - " + it));

        Mono.fromCallable(LocalDateTime::now)
            .subscribe(System.out::println);
    }
}
