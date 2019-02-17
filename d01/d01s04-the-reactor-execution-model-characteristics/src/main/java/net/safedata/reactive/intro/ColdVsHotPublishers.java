package net.safedata.reactive.intro;

import net.safedata.reactive.spring.domain.StoreSetup;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A few examples to understand the difference between cold and hot publishers
 *
 * @author bogdan.solga
 */
public class ColdVsHotPublishers {

    private static final List<String> PRODUCT_NAMES = StoreSetup.getProductNames();

    public static void main(String[] args) throws InterruptedException {
        coldPublisher();

        hotPublisher();

        coldIntoHot();
    }

    // the data is retrieved from the start
    private static void coldPublisher() {
        final Flux<String> namesPublisher = Flux.fromArray(PRODUCT_NAMES.toArray(new String[0]))
                                                .take(3);
        namesPublisher.subscribe(stringConsumer("[1]"));
        System.out.println();
        namesPublisher.subscribe(stringConsumer("[2]"));
    }

    // the data is retrieved from its current position
    private static void hotPublisher() {
        final AtomicInteger counter = new AtomicInteger();
        final Flux<String> counterPublisher = Flux.<String>generate(sink -> sink.next(counter.incrementAndGet() + ""))
                                                   .take(5);
        counterPublisher.subscribe(stringConsumer("[1]"));
        System.out.println();
        counterPublisher.subscribe(stringConsumer("[2]"));
    }

    // since the second subscriber subscribes later, it will get just the data emitted since then
    private static void coldIntoHot() throws InterruptedException {
        final ConnectableFlux<String> namesPublisher = Flux.range(0, 7)
                                                           .map(index -> index + " - " + PRODUCT_NAMES.get(index))
                                                           // the execution is moved on the .parallel() scheduler
                                                           .delayElements(Duration.ofMillis(200))
                                                           .publish();
        namesPublisher.subscribe(stringConsumer("[1]"), Throwable::printStackTrace);
        namesPublisher.connect();

        Thread.sleep(500);

        // Reactor is async by nature --> 'subscribe' is an instant operation, returns the control to the running thread immediately
        namesPublisher.subscribe(stringConsumer("[2]"), Throwable::printStackTrace);
        System.out.println();

        // delay the method execution time, to allow the publisher to publis and the async subscribers to receive data
        // (both subscribers are async)
        Thread.sleep(2000);
    }

    private static Consumer<String> stringConsumer(final String prefix) {
        return value -> System.out.println(prefix + " " + value);
    }
}
