package net.safedata.reactive.intro.subscriber;

import net.safedata.reactive.intro.dto.PostDTO;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

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
                System.out.println("Processing...");
            } catch (final InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void onNext(final PostDTO postDTO) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("[").append(postDTO.getAuthor()).append("] \t")
                .append("[").append(LocalTime.now().toString().split("\\.")[0]).append("] ")
                .append(postDTO.getMessage());

        LOGGER.info("{}", stringBuilder);
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
