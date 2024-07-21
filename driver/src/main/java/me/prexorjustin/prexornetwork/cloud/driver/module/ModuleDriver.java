package me.prexorjustin.prexornetwork.cloud.driver.module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter
public class ModuleDriver {

    private final ArrayList<ModuleLoader> loadedModules = new ArrayList<>();

    public void load() {
        ArrayList<String> modules = getModules();

        if (modules.isEmpty())
            Driver.getInstance().getTerminalDriver().log(
                    Type.MODULE,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("module-folder-is-empty")
            );

        modules.forEach(s -> {
            ModuleLoader moduleLoader = new ModuleLoader(s);
            moduleLoader.load();
            this.loadedModules.add(moduleLoader);
        });
    }

    public void unload() {
        this.loadedModules.forEach(ModuleLoader::unload);
        this.loadedModules.clear();
    }

    public void reload() {
        this.loadedModules.forEach(ModuleLoader::reload);

        getModules().stream().filter(s -> {
            boolean notFound = true;

            for (ModuleLoader loadedModule : this.loadedModules) {
                if (loadedModule.getJarName().equalsIgnoreCase(s)) {
                    notFound = false;
                    break;
                }
            }

            return notFound;
        }).forEach(s -> {
            ModuleLoader moduleLoader = new ModuleLoader(s);
            moduleLoader.load();
            this.loadedModules.add(moduleLoader);
        });
    }

    @SneakyThrows
    private ArrayList<String> getModules() {
        ArrayList<String> modules = new ArrayList<>();

        try (Stream<Path> stream = Files.list(Paths.get("./modules/"))) {
            stream.forEach(path -> {
                String fileName = path.toFile().getName();
                if (fileName.contains(".jar")) modules.add(fileName.split(".jar")[0]);
            });
        }

        return modules;
    }
}
