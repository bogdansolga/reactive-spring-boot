package net.safedata.reactive.intro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestingReactiveCode {

    @Test
    public void testingReactivePipelinesUsingSchedulers() {
        // the virtual time method uses a scheduler which tricks the Flux publisher about the actual delay,
        // so that it won't wait the actual amount
        StepVerifier.withVirtualTime(() -> Flux.range(0, 5)
                                               .delayElements(Duration.ofSeconds(1)))
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(5))
                    .expectNextCount(5)
                    .verifyComplete();
    }

    @Test
    public void testingReactivePipelinesUsingSchedulersWithLongerTimes() {
        StepVerifier.withVirtualTime(() -> Flux.range(0, 5)
                                               .delayElements(Duration.ofMinutes(1)))
                    .expectSubscription()
                    .thenAwait(Duration.ofHours(1))
                    .expectNextCount(5)
                    .verifyComplete();
    }
}
