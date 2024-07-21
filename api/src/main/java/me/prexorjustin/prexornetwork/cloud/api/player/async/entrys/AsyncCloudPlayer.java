package me.prexorjustin.prexornetwork.cloud.api.player.async.entrys;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.player.interfaces.ICloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerKick;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerMessage;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AsyncCloudPlayer extends ICloudPlayer {

    public AsyncCloudPlayer(String username, UUID uuid) {
        super(username, uuid);
    }

    @Override
    public ICloudService getProxyServer() {
        try {
            return CloudAPI.getInstance().getAsyncServicePool().getService(getCache().getProxy()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ICloudService getService() {
        try {
            return CloudAPI.getInstance().getAsyncServicePool().getService(getCache().getService()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInAPIPlayerMessage(getUsername(), message));
    }

    @Override
    public void connect(ICloudService cloudService) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInAPIPlayerConnect(getUsername(), cloudService.getName()));
    }

    @Override
    public void connect(ICloudPlayer cloudPlayer) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInAPIPlayerConnect(getUsername(), cloudPlayer.getService().getName()));
    }

    @Override
    public void disconnect(String message) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInAPIPlayerKick(getUsername(), message));
    }
}
