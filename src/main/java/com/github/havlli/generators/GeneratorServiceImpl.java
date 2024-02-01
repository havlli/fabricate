package com.github.havlli.generators;

import java.util.Random;

public class GeneratorServiceImpl implements GeneratorService {

    private final Random random;

    public GeneratorServiceImpl() {
        this.random = createRandom();
    }

    public Random createRandom() {
        return new Random(System.nanoTime());
    }

    @Override
    public String getRandomValueFrom(String[] values) {
        return values[random.nextInt(values.length)];
    }
}
