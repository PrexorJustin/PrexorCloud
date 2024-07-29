package me.prexorjustin.prexornetwork.cloud.driver;

import lombok.Getter;
import lombok.Setter;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.offline.OfflinePlayerCacheDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.GroupDriver;
import me.prexorjustin.prexornetwork.cloud.driver.language.LanguageDriver;
import me.prexorjustin.prexornetwork.cloud.driver.module.ModuleDriver;
import me.prexorjustin.prexornetwork.cloud.driver.storage.MessageStorage;
import me.prexorjustin.prexornetwork.cloud.driver.template.TemplateDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.TerminalDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;

@Getter
public class Driver {

    @Getter
    private static Driver instance;
    private final TemplateDriver templateDriver;
    private final GroupDriver groupDriver;
    private final ModuleDriver moduleDriver;
    private OfflinePlayerCacheDriver offlinePlayerCacheDriver;

    private final MessageStorage messageStorage;
    @Setter
    private TerminalDriver terminalDriver;
    @Setter
    private LanguageDriver languageDriver;
    private WebServer webServer;

    public Driver() {
        instance = this;
        this.messageStorage = new MessageStorage();
        this.templateDriver = new TemplateDriver();
        this.moduleDriver = new ModuleDriver();
        this.groupDriver = new GroupDriver();
    }

    public void runWebServer() {
        this.webServer = new WebServer();
    }

    public void initOfflinePlayerCacheDriver() {
        this.offlinePlayerCacheDriver = new OfflinePlayerCacheDriver();
    }
}
