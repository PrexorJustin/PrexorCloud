package me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys;

import lombok.NonNull;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInDispatchCommand;


public class CloudService extends ICloudService {

    public CloudService(String name, String groupName) {
        super(name, groupName);
    }

    @Override
    public void dispatchCommand(@NonNull String command) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInDispatchCommand(getName(), command));
    }

    @Override
    public void shutdown() {
        CloudAPI.getInstance().getServicePool().stopService(getName());
    }

    @Override
    public int getPlayerCount() {
        if (getGroup().getGroupType().equalsIgnoreCase("PROXY"))
            return CloudAPI.getInstance().getPlayerPool().getAllPlayerFromProxy(getName()).size();
        return CloudAPI.getInstance().getPlayerPool().getAllPlayerFromService(getName()).size();
    }
}
