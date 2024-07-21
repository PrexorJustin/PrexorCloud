package me.prexorjustin.prexornetwork.cloud.driver.module;

import lombok.Getter;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.module.config.ModuleConfiguration;
import me.prexorjustin.prexornetwork.cloud.driver.module.extension.IModuleLoader;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
public class ModuleLoader implements IModuleLoader {

    private final String jarName;
    private final File file;
    private Properties properties;
    private ModuleConfiguration configuration;

    public ModuleLoader(String jarName) {
        this.jarName = jarName;
        this.file = new File("./modules/" + jarName + ".jar");
    }

    @Override
    public void load() {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{this.file.toURI().toURL()}, this.getClass().getClassLoader())) {
            try (JarFile jarFile = new JarFile(this.file)) {
                JarEntry jarEntry = jarFile.getJarEntry("module.properties");

                if (jarEntry != null) {
                    try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8)) {
                        this.invokeModuleMethod(loader, reader, "load");
                        Driver.getInstance().getTerminalDriver().log(
                                Type.MODULE,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-load")
                                        .replace("%module_name%", configuration.name())
                                        .replace("%module_version%", configuration.version())
                                        .replace("%module_author%", configuration.author())
                        );
                    } catch (Exception exception) {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.MODULE,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-load-error")
                                        .replace("%module%", this.jarName)
                        );
                    }
                } else {
                    Driver.getInstance().getTerminalDriver().log(
                            Type.MODULE,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-no-config-found")
                                    .replace("%module%", this.configuration.name())
                    );
                }
            } catch (Exception exception) {
                Driver.getInstance().getTerminalDriver().log(
                        Type.MODULE,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-no-config-found")
                                .replace("%module%", getJarName())
                );
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void unload() {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader())) {
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry entry = jarFile.getJarEntry("module.properties");

                if (entry != null) {
                    try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(entry), StandardCharsets.UTF_8)) {
                        this.invokeModuleMethod(loader, reader, "unload");
                        Driver.getInstance().getTerminalDriver().log(
                                Type.MODULE,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-unload")
                                        .replace("%module%", configuration.name())
                        );
                    }
                } else {
                    Driver.getInstance().getTerminalDriver().log(
                            Type.MODULE,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-no-config-found")
                                    .replace("%module%", configuration.name())
                    );

                }
            } catch (Exception ignore) {
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        try (URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader())) {
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry entry = jarFile.getJarEntry("module.properties");

                if (entry != null) {
                    try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(entry), StandardCharsets.UTF_8)) {
                        this.invokeModuleMethod(loader, reader, "reload");
                        Driver.getInstance().getTerminalDriver().log(
                                Type.MODULE,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-reload")
                                        .replace("%module%", configuration.name())
                        );
                    }
                } else {
                    Driver.getInstance().getTerminalDriver().log(
                            Type.MODULE,
                            Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-no-config-found")
                                    .replace("%module%", configuration.name())
                    );

                }
            } catch (Exception ignore) {
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private void invokeModuleMethod(URLClassLoader loader, InputStreamReader reader, String method) {
        this.properties = new Properties();
        this.properties.load(reader);
        this.configuration = new ModuleConfiguration(
                this.properties.getProperty("name"),
                this.properties.getProperty("author"),
                this.properties.getProperty("main"),
                this.properties.getProperty("copy"),
                this.properties.getProperty("version")
        );

        Class<?> moduleMainClass = Class.forName(this.properties.getProperty("main"), true, loader);
        Method moduleLoadMethod = moduleMainClass.getDeclaredMethod(method);
        Object newInstance = moduleMainClass.getDeclaredConstructor().newInstance();

        moduleLoadMethod.invoke(newInstance);
    }
}
