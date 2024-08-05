package me.prexorjustin.prexornetwork.cloud.plugin.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;

@Getter
public abstract class PrexorCloudPlugin {

    private final LiveService service;

    public PrexorCloudPlugin() {
        new Driver();
        new PluginDriver();
        this.service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);

        CloudAPI.getInstance().setState(ServiceState.LOBBY, this.service.getName());
    }

}
