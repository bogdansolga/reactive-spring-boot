package net.safedata.reactive.spring.config;

import net.safedata.reactive.spring.domain.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;
import java.util.Random;

@Configuration
public class WebSocketConfig {

    private static final Random RANDOM = new Random(500);

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        final Flux<Product> productFlux = Flux.<Product>generate(sink -> sink.next(
                        new Product(RANDOM.nextInt(100), "A product with the tag " + RANDOM.nextInt(200), RANDOM.nextDouble() * 100)))
                  .delayElements(Duration.ofSeconds(1))
                  .doFinally(signal -> System.out.println("Goodbye, signal is '" + signal + "'"));

        return session -> session.send(productFlux.map(item -> session.textMessage(item.toString())));
    }

    @Bean
    public SimpleUrlHandlerMapping handlerMapping() {
        final SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();

        simpleUrlHandlerMapping.setUrlMap(Collections.singletonMap("/webSocket/product", webSocketHandler()));
        simpleUrlHandlerMapping.setOrder(10);

        return simpleUrlHandlerMapping;
    }
}
