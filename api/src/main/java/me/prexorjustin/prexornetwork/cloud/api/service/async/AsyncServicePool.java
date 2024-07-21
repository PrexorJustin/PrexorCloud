package me.prexorjustin.prexornetwork.cloud.api.service.async;

import lombok.NonNull;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.async.entrys.AsyncCloudService;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudServicePool;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInLaunchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInStopService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketLaunchServiceWithCustomTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncServicePool extends ICloudServicePool {

    public CompletableFuture<List<AsyncCloudService>> getServices() {
        return CompletableFuture.supplyAsync(() -> getConnectedServices().stream().map(AsyncCloudService.class::cast).collect(Collectors.toList()));
    }

    public CompletableFuture<AsyncCloudService> getService(@NonNull String name) {
        return CompletableFuture.supplyAsync(() -> ((AsyncCloudService) getConnectedServices().stream()
                .filter(asyncCloudService -> asyncCloudService.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null))
        );
    }

    public CompletableFuture<List<AsyncCloudService>> getServicesByGroup(@NonNull String group) {
        return CompletableFuture.supplyAsync(() -> getConnectedServices().stream()
                .filter(asyncCloudService -> asyncCloudService.getGroup() != null && asyncCloudService.getGroup().getName().equals(group))
                .map(AsyncCloudService.class::cast)
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean doesServiceExist(@NonNull String name) {
        return getConnectedServices().parallelStream().anyMatch(service -> service.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean registerService(ICloudService service) {
        if (getConnectedServices().parallelStream().noneMatch(service1 -> service1.getName().equals(service.getName()))) {
            getConnectedServices().add(service);
            return true;
        }

        return false;
    }

    @Override
    public boolean unregisterService(String service) {
        if (getConnectedServices().parallelStream().anyMatch(service1 -> service1.getName().equals(service))) {
            getConnectedServices().removeIf(service1 -> service1.getName().equals(service));
            return true;
        }

        return false;
    }

    @Override
    public void launchService(String group) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInLaunchService(group));
    }

    @Override
    public void launchService(String group, String template) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketLaunchServiceWithCustomTemplate(group, template));
    }

    @Override
    public void stopService(String service) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInStopService(service));
    }
}
