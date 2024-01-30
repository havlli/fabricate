package com.github.havlli;

import com.github.havlli.generator.OptimizedRandomGenerator;
import com.github.havlli.generator.RandomGenerator;
import com.github.havlli.store.LocaleStore;
import com.github.havlli.store.LocaleStoreFactory;

import java.util.Locale;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        LocaleStore localeStore = LocaleStoreFactory.getLocaleStore(Locale.ENGLISH);
        RandomGenerator randomGenerator = new RandomGenerator(localeStore);
        OptimizedRandomGenerator optimizedRandomGenerator = new OptimizedRandomGenerator(localeStore, 500);



//        for (int i = 0; i < 500; i++) {
//            String generated = randomGenerator.generate();
//            System.out.println(generated);
//        }


        IntStream.range(0, 500)
                .mapToObj(i -> randomGenerator.generatePerson())
                .forEach(System.out::println);

//        Map<String, Long> collect = IntStream.range(0, 500)
//                .mapToObj(i -> {
//                    String generated = optimizedRandomGenerator.generate();
//                    System.out.println(generated);
//
//                    return generated;
//                })
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//
//        AtomicInteger count = new AtomicInteger();
//        collect.forEach((k, v) -> {
//            if(v > 1) {
//                System.out.println(k + " " + v);
//                count.getAndIncrement();
//            }
//        });
//
//        System.out.println(count);
    }
}