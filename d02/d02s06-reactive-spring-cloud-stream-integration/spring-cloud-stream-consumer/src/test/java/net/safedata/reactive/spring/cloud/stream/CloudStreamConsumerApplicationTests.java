package net.safedata.reactive.spring.cloud.stream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudStreamConsumerApplicationTests {

	@Test
	@Ignore("no need to run without a started cluster")
	public void contextLoads() {
	}

}
