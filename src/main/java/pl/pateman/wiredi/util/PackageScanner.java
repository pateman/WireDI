package pl.pateman.wiredi.util;

import pl.pateman.wiredi.exception.DIException;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PackageScanner {
    private static final String CLASS_FILE_EXTENSION = ".class";

    private final Map<String, List<Class<?>>> classes;

    public PackageScanner() {
        classes = new ConcurrentHashMap<>();
    }

    private List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> result = new ArrayList<>();
        if (!directory.exists()) {
            return result;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return result;
        }

        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    result.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(CLASS_FILE_EXTENSION)) {
                    result.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - CLASS_FILE_EXTENSION.length())));
                }
            }
            return result;
        } catch (Exception ex) {
            throw new DIException(ex);
        }
    }

    private List<Class<?>> scanClasses(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            List<Class<?>> scannedClasses = new ArrayList<>();
            for (File directory : dirs) {
                scannedClasses.addAll(findClasses(directory, packageName));
            }
            return scannedClasses;
        } catch (Exception ex) {
            throw new DIException(ex);
        }
    }

    public List<Class<?>> getClasses(String packageName) {
        return classes.computeIfAbsent(packageName, this::scanClasses);
    }
}
