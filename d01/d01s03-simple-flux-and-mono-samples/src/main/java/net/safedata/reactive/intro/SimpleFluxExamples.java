package net.safedata.reactive.intro;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A few examples for the usage of the {@link Flux} Reactor publisher
 */
public class SimpleFluxExamples {

    private static final Logger LOGGER = Loggers.getLogger(SimpleFluxExamples.class);

    public static void main(String[] args) {
        simpleFluxExamples();
        if (true) return;

        fluxFromCollections();

        fluxFromStreams();
    }

    private static void simpleFluxExamples() {
        System.out.println("Nothing happens until we subscribe...");
        Flux.just("Welcome to the Flux publisher!");
            //.subscribe(System.out::println);

        if (true) return;

        Flux.just("Welcome to the Flux publisher! Let's see the current thread")
            .map(it -> Thread.currentThread().getName() + " - " + it)
            //.subscribe(System.out::println);
            .subscribe(new SimpleSubscriber());

        Flux.range(0, 5)
            .log()
            .take(3)
            .subscribe(System.out::println);
    }

    private static void fluxFromCollections() {
        Flux.fromIterable(Arrays.asList(1, 4, 8))
            .subscribe(System.out::println);

        Flux.fromArray("some values obtained after a processing".split("\\s"))
            .subscribe(System.out::println);
    }

    private static void fluxFromStreams() {
        Flux.fromStream(IntStream.range(0, 5)
                                 .boxed())
            //.map(it -> "[" + Thread.currentThread().getName() + "] - " + it)
            .map(it -> it * 3)
            .subscribe(System.out::println);
    }

    private static class SimpleSubscriber implements Subscriber<String> {

        @Override
        public void onSubscribe(Subscription subscription) {
            LOGGER.info("onSubscribe: {}", subscription.toString());
            subscription.request(1);
        }

        @Override
        public void onNext(String s) {
            LOGGER.info("onNext: {}", s);
        }

        @Override
        public void onError(Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }

        @Override
        public void onComplete() {
            LOGGER.info("onComplete");
        }
    }
}
