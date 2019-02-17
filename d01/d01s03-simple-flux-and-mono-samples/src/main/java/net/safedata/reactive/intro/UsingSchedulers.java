package net.safedata.reactive.intro;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class UsingSchedulers {

    public static void main(String[] args) {
        //publishOnScheduling();

        subscribeOnScheduling();
    }

    private static void publishOnScheduling() {
        Flux.interval(Duration.ofMillis(300))
            //.publishOn(Schedulers.newElastic("el"))
            //.publishOn(Schedulers.newParallel("par"))
            .publishOn(Schedulers.newSingle("one"))
            .map(it -> "[" + Thread.currentThread().getName() + "] --> " + it)
            //.buffer(3)
            //.take(Duration.ofSeconds(5))
            .subscribe(System.out::println);
    }

    private static void subscribeOnScheduling() {
        Flux.fromArray("I want a holiday, not just a weekend".split("\\s"))
            .map(it -> "[" + it + "]")
            .map(value -> Thread.currentThread().getName() + " - " + value)
            .subscribeOn(Schedulers.newSingle("s"))
            .subscribe(System.out::println);
    }
}
