package net.safedata.reactive.spring.service;

import net.safedata.reactive.spring.domain.entity.Product;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductSubscriber implements Subscriber<Product> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSubscriber.class);

    @Override
    public void onSubscribe(Subscription subscription) {
        LOGGER.debug("Subscribed to {}", subscription.toString());
        subscription.request(10);
    }

    @Override
    public void onNext(Product product) {
        LOGGER.info("New product received - '{}'", product);
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onComplete() {
        LOGGER.info("All complete");
    }
}
