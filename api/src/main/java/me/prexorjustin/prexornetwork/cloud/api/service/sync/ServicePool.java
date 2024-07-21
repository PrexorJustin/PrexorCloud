package me.prexorjustin.prexornetwork.cloud.api.service.sync;

import lombok.Getter;
import lombok.NonNull;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudServicePool;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInLaunchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInStopService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketLaunchServiceWithCustomTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ServicePool extends ICloudServicePool {

    public CloudService getService(@NonNull String name) {
        return ((CloudService) getConnectedServices().stream().filter(cloudService -> cloudService.getName().equalsIgnoreCase(name)).findFirst().orElse(null));
    }

    public List<CloudService> getServicesByGroup(@NonNull String group) {
        return getConnectedServices().stream()
                .filter(cloudService -> cloudService.getGroup() != null && cloudService.getGroup().getName().equalsIgnoreCase(group))
                .map(cloudService -> ((CloudService) cloudService))
                .collect(Collectors.toList());
    }

    @Override
    public boolean doesServiceExist(@NonNull String name) {
        return getConnectedServices().stream().anyMatch(service -> service.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean registerService(ICloudService service) {
        if (getConnectedServices().stream().noneMatch(service1 -> service1.getName().equals(service.getName()))) {
            getConnectedServices().add(service);
            return true;
        }

        return false;
    }

    @Override
    public boolean unregisterService(String service) {
        if (getConnectedServices().stream().anyMatch(service1 -> service1.getName().equals(service))) {
            getConnectedServices().removeIf(service1 -> service1.getName().equals(service));
            return true;
        }

        return false;
    }

    @Override
    public void launchService(String group) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInLaunchService(group));
    }

    @Override
    public void launchService(String group, String template) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketLaunchServiceWithCustomTemplate(group, template));
    }

    @Override
    public void stopService(String service) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInStopService(service));
    }

}
