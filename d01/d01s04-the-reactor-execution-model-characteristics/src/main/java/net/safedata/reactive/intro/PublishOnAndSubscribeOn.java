package net.safedata.reactive.intro;

import net.safedata.reactive.spring.domain.StoreSetup;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Consumer;

/**
 * A few examples to understand the usage of:
 *  - the 'publishOn' and 'subscribeOn' operators
 *  - the various schedulers on which worker threads can run on
 *
 * @author bogdan.solga
 */
public class PublishOnAndSubscribeOn {

    private static final List<String> PRODUCT_NAMES = StoreSetup.getProductNames();

    public static void main(String[] args) {
        //usingPublishOn();

        //System.out.println("-------------------------------------------------------------");

        //usingSubscribeOn();

        System.out.println("-------------------------------------------------------------");
    }

    private static void usingPublishOn() {
        reuseExistingSchedulers();

        //createNewSchedulers();
    }

    private static void usingSubscribeOn() {
        final Flux<String> namesPublisher = Flux.range(0, 5)
                                                .map(PublishOnAndSubscribeOn::getProductForIndex)
                                                .take(5);
        namesPublisher//.subscribeOn(Schedulers.single())
                      //.subscribeOn(Schedulers.elastic())
                      //.subscribeOn(Schedulers.parallel())
                      //.subscribeOn(Schedulers.immediate())
                      .subscribe(stringConsumer());
        sleepALittle();
    }

    private static void reuseExistingSchedulers() {
        final Flux<String> namesPublisher = Flux.range(0, 5)
                                                .map(PublishOnAndSubscribeOn::getProductForIndex)
                                                .publishOn(Schedulers.single())
                                                //.publishOn(Schedulers.elastic())
                                                //.publishOn(Schedulers.parallel())
                                                //.publishOn(Schedulers.immediate())
                                                .take(5);
        namesPublisher.subscribe(stringConsumer());
    }

    private static void createNewSchedulers() {
        final Scheduler newScheduler = Schedulers.newBoundedElastic(4, 100, "el");
        //final Scheduler newScheduler = Schedulers.newParallel("par", Runtime.getRuntime().availableProcessors() / 2);
        //final Scheduler newScheduler = Schedulers.newSingle("s");

        final Flux<String> namesPublisher = Flux.fromArray(PRODUCT_NAMES.toArray(new String[0]))
                                                .publishOn(newScheduler)
                                                .take(5);
        namesPublisher.subscribe(stringConsumer());

        sleepALittle();
        newScheduler.dispose();
    }

    private static Consumer<String> stringConsumer() {
        return value -> System.out.println("s: [" + Thread.currentThread().getName() + "] " + value + "\n");
    }

    private static String getProductForIndex(final int index) {
        final String product = PRODUCT_NAMES.get(index);
        System.out.println("p: [" + Thread.currentThread().getName() + "] -> '" + product + "' for index " + index);
        return product;
    }

    private static void sleepALittle() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
