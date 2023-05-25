package net.safedata.reactive.spring;

import net.safedata.reactive.spring.config.ProductController;
import net.safedata.reactive.spring.domain.Product;
import net.safedata.reactive.spring.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductService productService;

    @Test
    public void getSomeProductsReturnsItems() {
        when(productService.someProducts())
                .thenReturn(Flux.just(new Product(1, "The first product", 100)));

        webClient.get().uri("/product")
                 .accept(MediaType.APPLICATION_JSON)
                 .exchange()
                 .expectStatus()
                    .isOk()
                 .expectBody()
                    .jsonPath("$.length()").isEqualTo(1)
                    .jsonPath("$[0].id").isEqualTo(1);
    }
}
