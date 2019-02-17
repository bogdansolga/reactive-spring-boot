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
        StepVerifier.withVirtualTime(() -> Flux.range(0, 5)
                                               .delayElements(Duration.ofSeconds(1)))
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(5))
                    .expectNextCount(5)
                    .verifyComplete();
    }
}
