package com.github.havlli.generators.strategies;

import java.util.function.Function;

public interface GeneratorStrategy {
    Function<String[], String> generationStrategy();
}
