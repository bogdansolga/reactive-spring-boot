package net.safedata.reactive.spring.config;

import net.safedata.reactive.spring.domain.entity.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FunctionalConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        final Flux<Product> aSimpleProduct = Flux.just(new Product(1, "Tablet", 20));
        final Flux<Product> severalProducts = Flux.<Product>generate(sink -> sink.next(new Product(10, "Phone", 20))).take(5);
        final Flux<Product> productsStream =
                Flux.<Product>generate(sink -> sink.next(new Product(10, "The product for " + Instant.now(), 200)))
                        .delayElements(Duration.ofSeconds(1));

        final RouterFunction<ServerResponse> routerFunction = route(GET("/fn/product"),
                    request -> ServerResponse.ok().body(aSimpleProduct, Product.class))
                .andRoute(GET("/fn/many"), request -> ServerResponse.ok()
                                                                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                                                                    .body(severalProducts, Product.class))
                .andRoute(GET("/fn/stream"), request -> ServerResponse.ok()
                                                                      .contentType(MediaType.TEXT_EVENT_STREAM)
                                                                      .body(productsStream, Product.class));

        // dynamic linking / registration
        if (Math.random() < 10) {
            routerFunction.andRoute(GET("/random"), request -> ServerResponse.ok().body(Flux.just("Random URI"), String.class));
        }

        return routerFunction;
    }
}
