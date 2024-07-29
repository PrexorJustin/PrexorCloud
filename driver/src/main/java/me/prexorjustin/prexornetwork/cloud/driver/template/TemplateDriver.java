package me.prexorjustin.prexornetwork.cloud.driver.template;

import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.nodeconfig.NodeConfig;
import me.prexorjustin.prexornetwork.cloud.driver.template.interfaces.ITemplateDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.animation.AnimationDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TemplateDriver implements ITemplateDriver {

    @SneakyThrows
    @Override
    public void create(String template, boolean bungee, boolean isStatic) {
        if (isStatic) {
            Path templateLocation = Paths.get("./local/templates/" + template + "/default/");
            if (!Files.exists(templateLocation)) {
                Files.createDirectory(templateLocation);
                if (Files.exists(Paths.get("./service.json"))) {
                    ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

                    if (bungee)
                        Driver.getInstance().getMessageStorage().getPacketLoader().loadBungeecord(config.getBungeeVersion(), template + "/default");
                    else
                        Driver.getInstance().getMessageStorage().getPacketLoader().loadSpigot(config.getSpigotVersion(), template + "/default");
                    new AnimationDriver().play();
                } else {
                    NodeConfig config = (NodeConfig) new ConfigDriver("./nodeservice.json").read(NodeConfig.class);

                    if (bungee)
                        Driver.getInstance().getMessageStorage().getPacketLoader().loadBungeecord(config.getBungeeVersion(), template + "/default");
                    else
                        Driver.getInstance().getMessageStorage().getPacketLoader().loadSpigot(config.getSpigotVersion(), template + "/default");
                    new AnimationDriver().play();
                }
            }
        } else {
            Path templateLocation = Paths.get("./local/templates/" + template + "/");
            Files.createDirectories(templateLocation);
            if (Files.exists(Paths.get("./service.json"))) {
                ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

                if (bungee)
                    Driver.getInstance().getMessageStorage().getPacketLoader().loadBungeecord(config.getBungeeVersion(), template + "/default");
                else
                    Driver.getInstance().getMessageStorage().getPacketLoader().loadSpigot(config.getSpigotVersion(), template + "/default");

                new AnimationDriver().play();
            } else {
                NodeConfig config = (NodeConfig) new ConfigDriver("./nodeservice.json").read(NodeConfig.class);

                if (bungee)
                    Driver.getInstance().getMessageStorage().getPacketLoader().loadBungeecord(config.getBungeeVersion(), template + "/default");
                else
                    Driver.getInstance().getMessageStorage().getPacketLoader().loadSpigot(config.getSpigotVersion(), template + "/default");
                new AnimationDriver().play();
            }
        }
    }

    @SneakyThrows
    @Override
    public void copy(String template, String directory) {
        if (Files.exists(Paths.get("./local/templates/" + template + "/")))
            FileUtils.copyDirectory(new File("./local/templates/" + template + "/"), new File(directory));
    }

    @SneakyThrows
    @Override
    public void delete(String template) {
        Files.deleteIfExists(Paths.get("./local/templates/" + template + "/"));
    }

    @Override
    public void install(String template, boolean bungee) {
        if (Files.exists(Paths.get("./service.json"))) {
            ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

            if (bungee)
                Driver.getInstance().getMessageStorage().getPacketLoader().loadBungeecord(config.getBungeeVersion(), template);
            else
                Driver.getInstance().getMessageStorage().getPacketLoader().loadSpigot(config.getSpigotVersion().replace("-", "").replace(".", ""), template);
        } else {
            NodeConfig config = (NodeConfig) new ConfigDriver("./nodeservice.json").read(NodeConfig.class);

            if (bungee)
                Driver.getInstance().getMessageStorage().getPacketLoader().loadBungeecord(config.getBungeeVersion(), template);
            else
                Driver.getInstance().getMessageStorage().getPacketLoader().loadSpigot(config.getSpigotVersion().replace("-", "").replace(".", ""), template);
        }
    }

    @Override
    public ArrayList<String> get() {
        File[] files = new File("./local/templates/").listFiles();
        ArrayList<String> templates = new ArrayList<>();

        for (int i = 0; i != (files != null ? files.length : 0); i++) templates.add(files[i].getName());

        return templates;
    }
}
