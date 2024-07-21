package me.prexorjustin.prexornetwork.cloud.api;

import lombok.Getter;
import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.async.AsyncOfflinePlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.offlineplayer.sync.OfflinePlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.player.async.AsyncPlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.player.sync.PlayerPool;
import me.prexorjustin.prexornetwork.cloud.api.service.async.AsyncServicePool;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.ServicePool;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.service.LiveService;
import me.prexorjustin.prexornetwork.cloud.driver.event.EventDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDispatchMainCommand;

import java.util.concurrent.CompletableFuture;

@Getter
public class CloudAPI {

    @Getter
    private static CloudAPI instance;

    private final LiveService service;

    private ServicePool servicePool;
    private AsyncServicePool asyncServicePool;

    private PlayerPool playerPool;
    private AsyncPlayerPool asyncPlayerPool;

    private OfflinePlayerPool offlinePlayerPool;
    private AsyncOfflinePlayerPool asyncOfflinePlayerPool;

    private final RestDriver restDriver;
    private EventDriver eventDriver;

    public CloudAPI() {
        instance = this;

        new Driver();
        this.service = (LiveService) new ConfigDriver("./CLOUDSERVICE").read(LiveService.class);
        this.restDriver = new RestDriver(this.service.getManagerAddress(), this.service.getRestPort());

        CompletableFuture.runAsync(this::initializeLazyComponents);
    }

    private void initializeLazyComponents() {
        if (this.eventDriver == null) this.eventDriver = new EventDriver();
        if (this.servicePool == null) this.servicePool = new ServicePool();
        if (this.asyncServicePool == null) this.asyncServicePool = new AsyncServicePool();
        if (this.playerPool == null) this.playerPool = new PlayerPool();
        if (this.asyncPlayerPool == null) this.asyncPlayerPool = new AsyncPlayerPool();
        if (this.offlinePlayerPool == null) this.offlinePlayerPool = new OfflinePlayerPool();
        if (this.asyncOfflinePlayerPool == null) this.asyncOfflinePlayerPool = new AsyncOfflinePlayerPool();
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
