package net.safedata.reactive.intro;

import net.safedata.reactive.spring.domain.Product;
import net.safedata.reactive.spring.domain.StoreSetup;
import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A few examples to understand Project Reactor's {@link org.reactivestreams.Publisher} and {@link org.reactivestreams.Subscriber}
 * subscription model
 *
 * @author bogdan.solga
 */
public class NothingHappensUntilWeSubscribe {

    private static final Logger LOGGER = Loggers.getLogger(NothingHappensUntilWeSubscribe.class);

    private static final List<String> PRODUCT_NAMES;

    static {
        PRODUCT_NAMES = getProductNames();
    }

    private static List<String> getProductNames() {
        return StoreSetup.getDefaultStore()
                         .getStoreSections()
                         .stream()
                         .map(section -> section.getProducts()
                                                .orElse(new ArrayList<>()))
                         .flatMap(products -> products.stream()
                                                      .map(Product::getName))
                         .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        simpleGeneratedFlux();
    }

    private static String getProductForIndex(final int index) {
        System.out.println("[" + Thread.currentThread().getName() + "] Returning the product with the index " + index);
        return PRODUCT_NAMES.get(index);
    }

    private static void simpleGeneratedFlux() {
        Flux<String> someProductNames = Flux.range(0, PRODUCT_NAMES.size() - 4)
                                            .map(NothingHappensUntilWeSubscribe::getProductForIndex);
        //someProductNames.subscribe(value -> System.out.print(" -> " + value + "\n"));
        //someProductNames.subscribe();
        someProductNames.subscribe(System.out::println, Throwable::printStackTrace);
    }
}
