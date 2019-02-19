package net.safedata.reactive.spring;

import net.safedata.reactive.spring.domain.Product;
import net.safedata.reactive.spring.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringWebFluxContinuedApplication.class)
public class ReactiveSpringTrainingApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ProductService productService;

	private WebTestClient webTestClient;

	@Before
	public void setup() {
		webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
									 .configureClient()
									 .baseUrl("http://localhost:8080")
									 .build();
	}

	@Test
	public void givenProductsStream_WhenGettingTheStreamLength_ThenTheLengthIsCorrect() {
		StepVerifier.withVirtualTime(() -> productService.productsStream()
														 .take(10)
														 .collectList())
					.thenAwait(Duration.ofMinutes(1))
					.consumeNextWith(list -> Assert.assertEquals(list.size(), 10))
					.verifyComplete();
	}

	@Test
	public void givenTheManyProducts_WhenGettingTheProducts_ThenTheResponseContentIsCorrect() {
		StepVerifier.create(productService.someProducts())
					.expectNext(new Product(1, "The first product", 100))
					.expectNext(new Product(2, "The second product", 200))
					.expectNext(new Product(3, "The third product", 300))
					.verifyComplete();
	}

	@Test
	public void givenTheMultipleProductsEndpointExposesProducts_whenGettingTheProducts_thenAllGood() {
		webTestClient.get()
					 .uri("/fn/many")
					 .accept(MediaType.APPLICATION_JSON_UTF8)
					 .exchange() // the only blocking call --> we don't want the test to be done before retrieving the response
					 .expectStatus()
					 	.isOk()
					 .expectHeader()
					 	.contentType(MediaType.APPLICATION_JSON_UTF8)
					 //.expectBodyList(Product.class).hasSize(5)
					 .expectBody()
					 	.jsonPath("$.length()").isEqualTo(5)
					 	.jsonPath("$[0].id").isEqualTo(10)
		;
	}
}
