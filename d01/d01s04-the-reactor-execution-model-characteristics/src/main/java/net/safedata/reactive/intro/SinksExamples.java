package net.safedata.reactive.intro;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class SinksExamples {

    public static void main(String[] args) {
        //sinkOne();

        sinkMany();
    }

    private static void sinkMany() {
        final Sinks.Many<String> manySink = Sinks.many()
                                                 .multicast()
                                                 .directBestEffort();
        final Flux<String> flux = manySink.asFlux();

        flux.subscribe(
                item -> System.out.println(item),
                err -> System.err.println(err.getMessage()),
                () -> System.out.println("On complete")
        );

        manySink.tryEmitNext("something").orThrow();
        manySink.tryEmitNext("something else").orThrow();

        flux.subscribe(
                item -> System.out.println("2: " + item),
                err -> System.err.println(err.getMessage()),
                () -> System.out.println("2: on complete")
        );

        manySink.emitNext("the end", Sinks.EmitFailureHandler.FAIL_FAST);
        manySink.tryEmitComplete();
    }

    private static void sinkOne() {
        final Sinks.One<String> one = Sinks.one();
        final Mono<String> mono = one.asMono();

        one.emitValue("something", Sinks.EmitFailureHandler.FAIL_FAST);
        one.emitValue("else", Sinks.EmitFailureHandler.FAIL_FAST);
        mono.subscribe(item -> System.out.println(item));
    }
}
