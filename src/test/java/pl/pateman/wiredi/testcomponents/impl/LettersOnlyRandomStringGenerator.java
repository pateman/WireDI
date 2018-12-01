package pl.pateman.wiredi.testcomponents.impl;

import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.testcomponents.RandomStringGenerator;

import java.util.Random;

@WireComponent(name = "lettersOnlyRandomStringGenerator", multiple = true)
public class LettersOnlyRandomStringGenerator extends AbstractRandomStringGenerator implements RandomStringGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST";

    private final Random random;

    public LettersOnlyRandomStringGenerator() {
        random = new Random();
    }

    @Override
    public String generate() {
        return getRandomString(ALPHABET, random, 6);
    }
}
