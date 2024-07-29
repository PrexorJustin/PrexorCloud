package me.prexorjustin.prexornetwork.cloud.launcher.loader;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class InstanceLoader {

    public InstanceLoader(File file) {
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry entry = jarFile.getJarEntry("able.properties");
                if (entry != null) {
                    try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(entry), StandardCharsets.UTF_8)) {
                        Properties properties = new Properties();
                        properties.load(reader);
                        Class classEntry = Class.forName(properties.getProperty("main"), true, loader);
                        Method method = classEntry.getDeclaredMethod("run");
                        Object instance = classEntry.getConstructor().newInstance();
                        method.invoke(instance);
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
