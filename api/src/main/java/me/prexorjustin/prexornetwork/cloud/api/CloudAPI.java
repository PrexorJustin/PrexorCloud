package me.prexorjustin.prexornetwork.cloud.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.group.async.AsyncGroupPool;
import me.prexorjustin.prexornetwork.cloud.api.group.sync.GroupPool;
import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.async.AsyncOfflinePlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.sync.OfflinePlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.player.async.AsyncPlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.player.sync.PlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.service.async.AsyncServicePool;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.ServicePool;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.event.EventDriver;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.whitelist.Whitelist;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInChangeState;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDispatchMainCommand;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
public class CloudAPI {

    @Getter
    private static CloudAPI instance;

    private LiveService service;

    private ServicePool servicePool;
    private AsyncServicePool asyncServicePool;

    private PlayerPool playerPool;
    private AsyncPlayerPool asyncPlayerPool;

    private OfflinePlayerPool offlinePlayerPool;
    private AsyncOfflinePlayerPool asyncOfflinePlayerPool;

    private GroupPool groupPool;
    private AsyncGroupPool asyncGroupPool;

    private RestDriver restDriver;
    private EventDriver eventDriver;

    public CloudAPI() {
        instance = this;

        initializeCoreComponents();
        CompletableFuture.runAsync(this::initializeLazyComponents);
    }

    private void initializeCoreComponents() {
        new Driver();
        this.service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        this.restDriver = new RestDriver(this.service.getManagerAddress(), this.service.getRestPort());
    }

    private void initializeLazyComponents() {
        if (this.eventDriver == null) this.eventDriver = new EventDriver();
        if (this.servicePool == null) this.servicePool = new ServicePool();
        if (this.asyncServicePool == null) this.asyncServicePool = new AsyncServicePool();
        if (this.playerPool == null) this.playerPool = new PlayerPool();
        if (this.asyncPlayerPool == null) this.asyncPlayerPool = new AsyncPlayerPool();
        if (this.offlinePlayerPool == null) this.offlinePlayerPool = new OfflinePlayerPool();
        if (this.asyncOfflinePlayerPool == null) this.asyncOfflinePlayerPool = new AsyncOfflinePlayerPool();
        if (this.groupPool == null) this.groupPool = new GroupPool();
        if (this.asyncGroupPool == null) this.asyncGroupPool = new AsyncGroupPool();
    }

    public CloudService getCloudService() {
        initializeLazyComponents();
        return this.servicePool.getService(this.service.getName());
    }

    public Set<String> getWhitelist() {
        initializeLazyComponents();
        return ((Whitelist) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(WebServer.Routes.WHITELIST.getRoute()), Whitelist.class)).getWhitelist();
    }

    public Messages getMessageConfig() {
        initializeLazyComponents();
        return (Messages) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/message/default"), Messages.class);
    }

    public void setState(ServiceState state, String name) {
        initializeLazyComponents();
        sendPacketSynchronized(new PacketInChangeState(name, state.name()));
    }

    public void dispatchCommand(String command) {
        this.sendPacketSynchronized(new PacketInDispatchMainCommand(command));
    }

    public void sendPacketSynchronized(Packet packet) {
        NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(packet);
    }

    public void sendPacketAsynchronous(Packet packet) {
        NettyDriver.getInstance().getNettyClient().sendPacketsAsynchronous(packet);
    }
}
