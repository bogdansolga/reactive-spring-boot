package net.safedata.reactive.spring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux

@SpringBootApplication
class ReactiveFunctionalSpring

fun main(args: Array<String>) {
    SpringApplicationBuilder()
            .sources(ReactiveFunctionalSpring::class.java)
            .initializers(beans {
                bean {
                    router {
                        GET("/fn/kotlin") {
                            ServerResponse.ok().body(Flux.just("Less is more, the Kotlin way"), String::class.java)
                        }
                    }
                }
            })
            .run(*args)
}
