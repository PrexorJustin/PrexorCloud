package me.prexorjustin.prexornetwork.cloud.api.service.interfaces;


import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;

@Getter
public abstract class ICloudServicePool {

    private final ArrayList<ICloudService> connectedServices = new ArrayList<>();

    public abstract boolean doesServiceExist(@NonNull String name);

    public abstract boolean registerService(ICloudService service);

    public abstract boolean unregisterService(String service);

    public abstract void launchService(String group);

    public abstract void launchService(String group, String template);

    public abstract void stopService(String service);
}
