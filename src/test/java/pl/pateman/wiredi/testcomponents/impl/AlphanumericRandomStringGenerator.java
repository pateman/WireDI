package pl.pateman.wiredi.testcomponents.impl;

import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.testcomponents.RandomStringGenerator;

import java.util.Random;

@WireComponent(name = "alphanumericRandomStringGenerator", multiple = true)
public class AlphanumericRandomStringGenerator extends AbstractRandomStringGenerator implements RandomStringGenerator {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz****";

    private final Random random;

    private AlphanumericRandomStringGenerator() {
        random = new Random();
    }

    @Override
    public String generate() {
        return getRandomString(ALPHABET, random, 6);
    }
}
