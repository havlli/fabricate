package com.github.havlli.generators.strategies;

import java.util.Random;
import java.util.function.Function;

public class SimpleRandomGeneratorStrategy implements GeneratorStrategy {

    private final Random random;

    public SimpleRandomGeneratorStrategy() {
        this.random = createRandom();
    }

    public Random createRandom() {
        return new Random(System.nanoTime());
    }

    @Override
    public Function<String[], String> generationStrategy() {
        return values -> values[random.nextInt(values.length)];
    }
}
