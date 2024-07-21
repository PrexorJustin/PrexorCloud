package me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.prexorjustin.prexornetwork.cloud.api.bootstrap.bungee.BungeeBootstrap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class ServerConfigUtil {

    private File file;
    private Configuration configuration;
    private boolean locked;

    static {
        setupConfig();
        if (locked)
            ProxyServer.getInstance().getScheduler().schedule(BungeeBootstrap.getInstance(), ServerConfigUtil::setupConfig, 5L, TimeUnit.SECONDS);
    }

    public void addServerToConfig(ServerInfo serverInfo) {
        if (locked) return;

        configuration.set("servers." + serverInfo.getName() + ".motd", serverInfo.getMotd().replace(ChatColor.COLOR_CHAR, '&'));
        configuration.set("servers." + serverInfo.getName() + ".address", serverInfo.getSocketAddress().toString());
        configuration.set("servers." + serverInfo.getName() + ".restricted", false);

        saveConfig();
    }

    public void addLobbyServerToConfig(ServerInfo serverInfo) {
        if (locked) return;

        List<String> stringList = configuration.getStringList("listeners.priorities");
        stringList.add(serverInfo.getName());
        configuration.set("listeners.priorities", stringList);

        addServerToConfig(serverInfo);
    }

    public void removeServerFromConfig(String serverName) {
        if (locked) return;

        configuration.set("servers." + serverName, null);

        saveConfig();
    }

    public void removeLobbyFromConfig(String lobbyName) {
        if (locked) return;

        List<String> stringList = configuration.getStringList("listeners.priorities");
        stringList.remove(lobbyName);

        configuration.set("listeners.priorities", stringList);

        removeServerFromConfig(lobbyName);
    }

    @SneakyThrows
    private void saveConfig() {
        if (locked) return;

        YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, file);
    }

    @SneakyThrows
    private void setupConfig() {
        FileInputStream inputStream = null;
        InputStreamReader streamReader = null;
        try {
            file = new File(ProxyServer.getInstance().getPluginsFolder().getParentFile(), "config.yml");

            inputStream = new FileInputStream(file);
            streamReader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1);

            configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(streamReader);
        } catch (IOException ignored) {

        } finally {
            if (inputStream != null) inputStream.close();
            if (streamReader != null) streamReader.close();
        }

        locked = configuration == null;
    }
}
