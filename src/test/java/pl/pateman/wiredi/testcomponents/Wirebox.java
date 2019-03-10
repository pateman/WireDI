package pl.pateman.wiredi.testcomponents;

import pl.pateman.wiredi.annotation.WireComponent;
import pl.pateman.wiredi.annotation.Wires;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

@Wires
public class Wirebox {

    @WireComponent
    private static Random someRandom() {
        return new Random();
    }

    @WireComponent(name = "dateFormatter")
    public static DateFormat simpleDateFormat() {
        return new SimpleDateFormat("YYYY-MM-dd");
    }

    public static Calendar thisShouldNotBeDetected() {
        return Calendar.getInstance();
    }

    @WireComponent(multiple = true)
    public static RequiresARandom requiresARandom(Random random) {
        return new RequiresARandom(random);
    }

    public static class RequiresARandom {
        private final Random random;

        public RequiresARandom(Random random) {
            this.random = random;
        }

        public boolean hasRandom() {
            return random != null;
        }
    }
}
