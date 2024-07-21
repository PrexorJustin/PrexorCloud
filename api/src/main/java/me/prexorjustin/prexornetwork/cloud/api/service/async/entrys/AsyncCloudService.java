package me.prexorjustin.prexornetwork.cloud.api.service.async.entrys;

import lombok.NonNull;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDispatchCommand;

public class AsyncCloudService extends ICloudService {

    public AsyncCloudService(String name, String groupName) {
        super(name, groupName);
    }

    @Override
    public void dispatchCommand(@NonNull String command) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInDispatchCommand(getName(), command));
    }

    @Override
    public void shutdown() {
        CloudAPI.getInstance().getAsyncServicePool().stopService(getName());
    }

    @SneakyThrows
    @Override
    public int getPlayerCount() {
        if (getGroup().getGroupType().equalsIgnoreCase("PROXY"))
            return CloudAPI.getInstance().getAsyncPlayerPool().getAllPlayerFromProxy(getName()).get().size();
        return CloudAPI.getInstance().getAsyncPlayerPool().getAllPlayerFromService(getName()).get().size();
    }
}
