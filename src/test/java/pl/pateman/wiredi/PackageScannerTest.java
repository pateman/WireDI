package pl.pateman.wiredi;

import org.junit.Test;
import pl.pateman.wiredi.testcomponents.dto.User;
import pl.pateman.wiredi.util.PackageScanner;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class PackageScannerTest {

    private PackageScanner givenScanner() {
        return new PackageScanner();
    }

    @Test
    public void shouldReturnAllClassesInAPackage() {
        PackageScanner packageScanner = givenScanner();

        List<Class<?>> classes = packageScanner.getClasses("pl.pateman.wiredi.testcomponents.dto");

        assertFalse(classes.isEmpty());
        assertThat(classes, hasItem(User.class));
    }

    @Test
    public void shouldScanDeeply() {
        PackageScanner packageScanner = givenScanner();

        List<Class<?>> classes = packageScanner.getClasses("pl.pateman.wiredi.testcomponents");

        assertFalse(classes.isEmpty());
        assertThat(classes, hasItem(User.class));
    }
}
