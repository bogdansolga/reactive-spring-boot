package net.safedata.reactive.intro.subscriber;

import net.safedata.reactive.intro.dto.PostDTO;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostSubscriber implements Subscriber<PostDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostSubscriber.class);

    private final String id;
    private final int batchSize;

    private Subscription subscription;

    public PostSubscriber(final String id, final int batchSize) {
        this.id = id; this.batchSize = batchSize;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        LOGGER.debug("{} has subscribed, batch size is {}", id, batchSize);
        this.subscription = subscription;
        subscription.request(batchSize);

        // simulate a random long running operation
        if (System.currentTimeMillis() % 2 != 0) {
            try {
                Thread.sleep(300);
                LOGGER.info("Performing a long running processing...");
            } catch (final InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onNext(final PostDTO postDTO) {
        LOGGER.info("{}", postDTO);
        subscription.request(batchSize);
    }

    @Override
    public void onError(final Throwable throwable) {
        LOGGER.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        LOGGER.debug("Completed requesting {} items", batchSize);
    }
}
