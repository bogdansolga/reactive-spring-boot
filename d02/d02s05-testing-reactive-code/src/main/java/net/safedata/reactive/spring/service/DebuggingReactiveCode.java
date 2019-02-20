package net.safedata.reactive.spring.service;

import net.safedata.reactive.spring.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;

public class DebuggingReactiveCode {

    public static void main(String[] args) {
        Hooks.onOperatorDebug();

        Flux.just(new Product(1, null, 100))
            .doOnError(EmailSender::sendEmail)

            .checkpoint("before the issue", true)
            .map(it -> it.getName().length())
            .checkpoint("an error occurred here", true)

            .map(value -> value * 3)
            .subscribe(System.out::println);
    }

    private static class EmailSender {
        static void sendEmail(Throwable throwable) {}
    }
}
