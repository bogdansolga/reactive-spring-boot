package net.safedata.reactive.intro;

import net.safedata.reactive.spring.domain.StoreSetup;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * A few examples to understand Project Reactor's subscription model
 *
 * @author bogdan.solga
 */
public class NothingHappensUntilWeSubscribe {

    private static final List<String> PRODUCT_NAMES = StoreSetup.getProductNames();

    public static void main(String[] args) {
        Flux<String> someProductNames = Flux.range(0, PRODUCT_NAMES.size() - 4)
                                            .map(NothingHappensUntilWeSubscribe::getProductForIndex);
        //someProductNames.subscribe(value -> System.out.print(" -> " + value + "\n"));
        //someProductNames.subscribe();
        someProductNames.subscribe(System.out::println, Throwable::printStackTrace);
    }

    private static String getProductForIndex(final int index) {
        System.out.println("[" + Thread.currentThread().getName() + "] Returning the product with the index " + index);
        return PRODUCT_NAMES.get(index);
    }
}
