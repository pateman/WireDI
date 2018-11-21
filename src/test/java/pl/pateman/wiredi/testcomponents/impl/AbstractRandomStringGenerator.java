package pl.pateman.wiredi.testcomponents.impl;

import java.util.Random;

abstract class AbstractRandomStringGenerator {

    protected String getRandomString(String alphabet, Random random, int length) {
        char[] chars = alphabet.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }
}
