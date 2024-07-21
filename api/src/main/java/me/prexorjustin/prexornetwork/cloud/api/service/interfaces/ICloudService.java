package me.prexorjustin.prexornetwork.cloud.api.service.interfaces;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServiceList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServices;

@RequiredArgsConstructor
@Getter
public abstract class ICloudService {

    private final String name, groupName;

    public abstract void dispatchCommand(@NonNull String command);

    public abstract void shutdown();

    public abstract int getPlayerCount();

    public String getID() {
        LiveServiceList liveServiceList = (LiveServiceList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudservice/general"), LiveServiceList.class);

        return getName().replace(getGroupName(), "").replace(liveServiceList.getCloudServiceSplitter(), "");
    }

    public void sync() {
        CloudAPI.getInstance().dispatchCommand("service copy " + getName());
    }

    public Group getGroup() {
        return (Group) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudgroup/" + this.groupName), Group.class);
    }

    public boolean isTypeLobby() {
        return getGroup().getGroupType().equalsIgnoreCase("LOBBY");
    }

    public boolean isStatic() {
        return getGroup().isRunStatic();
    }

    public ServiceState getState() {
        return getLiveServices().getState();
    }

    public String getAddress() {
        return getLiveServices().getHost();
    }

    public Integer getPort() {
        return getLiveServices().getPort();
    }

    private LiveServices getLiveServices() {
        LiveServiceList list = (LiveServiceList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudservice/general"), LiveServiceList.class);
        return (LiveServices) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudservice/" + getName().replace(list.getCloudServiceSplitter(), "~")), LiveServices.class);
    }

    public String toString() {
        return "name='" + getName() + "', group='" + getGroupName() + "', state='" + getState() + "', address='" + getAddress() + "', port='" + getPort() + "', playerCount='" + getPlayerCount() + "'";
    }
}
