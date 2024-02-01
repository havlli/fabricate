package com.github.havlli.generators;

import java.util.Random;

public interface GeneratorService {
    String getRandomValueFrom(String[] values);
    Random createRandom();
}
