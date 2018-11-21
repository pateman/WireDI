package pl.pateman.wiredi.testcomponents.impl;

import pl.pateman.wiredi.WireComponent;
import pl.pateman.wiredi.testcomponents.RandomStringGenerator;

import java.util.Random;

@WireComponent(name = "lettersOnlyRandomStringGenerator", multiple = true)
public class LettersOnlyRandomStringGenerator extends AbstractRandomStringGenerator implements RandomStringGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST";

    private final Random random;

    private LettersOnlyRandomStringGenerator() {
        random = new Random();
    }

    @Override
    public String generate() {
        return getRandomString(ALPHABET, random, 6);
    }
}