package com.github.havlli;

import com.github.havlli.store.EnglishLocaleStore;
import com.github.havlli.store.LocaleStore;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        LocaleStore localeStore = new EnglishLocaleStore();
        RandomGenerator randomGenerator = new RandomGenerator(localeStore);



        for (int i = 0; i < 500; i++) {
            String generated = randomGenerator.generate();
            System.out.println(generated);
        }

        Map<String, Long> collect = IntStream.range(0, 500)
                .mapToObj(i -> {
                    String generated = randomGenerator.generate();
                    System.out.println(generated);

                    return generated;
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        AtomicInteger count = new AtomicInteger();
        collect.forEach((k, v) -> {
            if(v > 1) {
                System.out.println(k + " " + v);
                count.getAndIncrement();
            }
        });

        System.out.println(count);
    }
}