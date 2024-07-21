package me.prexorjustin.prexornetwork.cloud.api.player.sync.entrys;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.player.interfaces.ICloudPlayer;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerKick;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.player.api.PacketInAPIPlayerMessage;

import java.util.UUID;

public class CloudPlayer extends ICloudPlayer {

    public CloudPlayer(String username, UUID uuid) {
        super(username, uuid);
    }

    @Override
    public ICloudService getProxyServer() {
        return CloudAPI.getInstance().getServicePool().getService(getCache().getProxy());
    }

    @Override
    public ICloudService getService() {
        return CloudAPI.getInstance().getServicePool().getService(getCache().getService());
    }

    @Override
    public void sendMessage(String message) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerMessage(getUsername(), message));
    }

    @Override
    public void connect(ICloudService cloudService) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerConnect(getUsername(), cloudService.getName()));
    }

    @Override
    public void connect(ICloudPlayer cloudPlayer) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerConnect(getUsername(), cloudPlayer.getService().getName()));
    }

    @Override
    public void disconnect(String message) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerKick(getUsername(), message));
    }
}
