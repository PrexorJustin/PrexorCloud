package me.prexorjustin.prexornetwork.cloud.launcher.loader;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class InstanceLoader {

    public void loadInstance(File file) {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, InstanceLoader.class.getClassLoader())) {
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry jarEntry = jarFile.getJarEntry("able.properties");

                try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8)) {
                    Properties properties = new Properties();
                    properties.load(reader);

                    Class<?> main = Class.forName(properties.getProperty("main"), true, loader);
                    Method run = main.getDeclaredMethod("run");
                    Object instance = main.getDeclaredConstructor().newInstance();

                    run.invoke(instance);
                }
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }

}
