package net.safedata.reactive.intro;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * A few examples for the usage of the {@link Flux} Reactor publisher
 */
public class SimpleFluxExamples {

    private static final Logger LOGGER = Loggers.getLogger(SimpleFluxExamples.class);

    public static void main(String[] args) {
        //blockingCalls();

        //deferredExecution();

        //parallelFlux();

        //backPressure();

        //errorHandling();

        //checkPointUsage();

        usingHooks();

        if (true) return;

        newFluxExamples();

        simpleFluxExamples();

        fluxFromCollections();

        fluxFromStreams();
    }

    private static void errorHandling() {
        Flux<String> flux = Flux.fromArray("a lot of items".split(" "))
                                .map(item -> processItem(item))
                                .doOnError(err -> System.err.println(err.getMessage()))
                                .onErrorReturn("the default")
                                .onErrorResume(RuntimeException.class, err -> Flux.just("error"))
                                .checkpoint("before finally")
                                .doFinally(signal -> System.out.println("The signal is " + signal));
        flux.subscribe(
                item -> System.out.println(item),
                err -> System.out.println(err.getMessage()),
                () -> System.out.println("complete"));
    }

    private static void checkPointUsage() {
        Flux<String> flux = Flux.fromArray("a lot of items".split(" "))
                                .checkpoint("before processing")
                                .map(SimpleFluxExamples::processItem)
                                .checkpoint("before doFinally")
                                .doFinally(signal -> System.out.println("The signal is " + signal));
        flux.subscribe(item -> System.out.println(item));
    }

    private static void usingHooks() {
        Hooks.onOperatorDebug();
        Flux<String> flux = Flux.fromArray("a lot of items".split(" "))
                                .map(SimpleFluxExamples::processItem);
        flux.subscribe(item -> System.out.println(item));
        Hooks.resetOnOperatorDebug();
    }

    private static String processItem(String item) {
        if (item.length() < 3) throw new RuntimeException("Too short");
        return item;
    }

    private static void backPressure() {
        Flux<String> flux = Flux.fromArray("a lot of items".split(" "))
                                .limitRate(200, 300)
                                .onBackpressureBuffer(20, BufferOverflowStrategy.DROP_OLDEST);

    }

    private static void parallelFlux() {
        List<String> strings = Arrays.asList("just some values".split(" "));
        //Stream<String> seqOrParallel = StreamSupport.stream(strings.spliterator(), strings.size() > 100);

        ParallelFlux<String> parallel = Flux.fromIterable(strings)
                                            .parallel()
                                            .log()
                                            .runOn(Schedulers.parallel());
        parallel.subscribe(item -> System.out.println(Thread.currentThread().getName() + ": " + item));

        sleepALittle();
    }

    private static void sleepALittle() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deferredExecution() {
        Random random = new Random(200);
        Flux<Integer> deferred = Flux.defer(() -> Flux.just(random.nextInt(2000)));

        deferred.subscribe(System.out::println);
        deferred.subscribe(System.out::println);
    }

    private static void blockingCalls() {
        Flux<String> flux = Flux.fromArray("Using a lot of items in a Flux".split(" "));
        System.out.println(flux.blockFirst());
        System.out.println(flux.blockLast());
    }

    private static void newFluxExamples() {
        Flux<String> first = Flux.just("First Flux");
        first.subscribe(item -> System.out.println(item));

        Flux<String> second = Flux.fromArray("A few words to be processed".split(" "))
                                  .filter(word -> word.length() > 3)
                                  .retry(10)
                                  .publishOn(Schedulers.fromExecutor(ForkJoinPool.commonPool()))
                                  //.delayElements(Duration.ofMillis(200), Schedulers.fromExecutor(ForkJoinPool.commonPool()))
                                  .map(String::toUpperCase); // a processing pipeline
        second//.subscribeOn(Schedulers.boundedElastic())
              .subscribe(item -> System.out.println(Thread.currentThread().getName() + " - " + item));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (true) return;
        second.subscribe(System.out::println); // first subscriber
        final Disposable onComplete = second.subscribe( // second subscriber
                System.out::println,
                err -> System.err.println(err.getMessage()),
                () -> System.out.println("On complete")
        );
        onComplete.dispose();
    }

    //.retryWhen(Retry.backoff(20, Duration.ofMillis(200)))

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
