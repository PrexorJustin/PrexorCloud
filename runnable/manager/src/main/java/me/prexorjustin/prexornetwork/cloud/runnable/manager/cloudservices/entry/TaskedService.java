package me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyPreparedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServicePreparedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceProcess;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServiceList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServices;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServicePrepared;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.interfaces.ITaskedService;

import java.util.Timer;

public class TaskedService implements ITaskedService {

    private final TaskedEntry entry;

    private ServiceProcess process;
    private boolean hasStartedNew;
    private Timer timer;

    public TaskedService(TaskedEntry entry) {
        this.entry = entry;
        this.hasStartedNew = false;
        this.timer = new Timer();

        LiveServices liveServices = new LiveServices();
        liveServices.setGroup(entry.getGroupName());
        liveServices.setName(entry.getServiceName());
        liveServices.setPlayers(0);
        liveServices.setHost(PrexorCloudManager.config.getNodes().stream().filter(managerConfigNodes -> managerConfigNodes.getName().equals(entry.getNode())).toList().get(0).getAddress());
        liveServices.setNode(entry.getTaskNode());
        liveServices.setPort(-1);
        liveServices.setUuid(entry.getUseId());
        liveServices.setState(ServiceState.QUEUED);
        liveServices.setLastReaction(-1);

        LiveServiceList list = (LiveServiceList) new ConfigDriver().convert(PrexorCloudManager.restDriver.get("/cloudservice/general"), LiveServiceList.class);
        list.getCloudServices().add(entry.getServiceName());

        Driver.getInstance().getWebServer().updateRoute("/cloudservice/general", new ConfigDriver().convert(list));
        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/cloudservice/" + entry.getServiceName().replace(CloudManager.config.getSplitter(), "~"), new ConfigDriver().convert(liveServices)));

        if (Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getGroupType().equals("PROXY")) {
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutServicePrepared(entry.getServiceName(), true, entry.getGroupName(), entry.getTaskNode()));
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudProxyPreparedEvent(entry.getServiceName(), entry.getGroupName(), entry.getTaskNode()));
        } else {
            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutServicePrepared(entry.getServiceName(), false, entry.getGroupName(), entry.getTaskNode()));
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudServicePreparedEvent(entry.getServiceName(), entry.getGroupName(), entry.getTaskNode()));
        }
    }
}
