package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.service;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyConnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudProxyDisconnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceConnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.service.CloudServiceDisconnectedEvent;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.process.ServiceState;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.PacketInServiceDisconnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInChangeState;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInLaunchService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketInStopService;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.cloudapi.PacketLaunchServiceWithCustomTemplate;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.PacketOutServiceConnected;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedEntry;

import java.util.TimerTask;

public class PacketInServiceHandler implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInLaunchService packetCast) {
            Group group = Driver.getInstance().getGroupDriver().load(packetCast.getGroup());
            String id = "";

            if (PrexorCloudManager.config.getUuid().equals("INT"))
                id = String.valueOf(PrexorCloudManager.serviceDriver.getFreeUUID(packetCast.getGroup()));
            else if (PrexorCloudManager.config.getUuid().equals("RANDOM"))
                id = PrexorCloudManager.serviceDriver.getFreeUUID();

            PrexorCloudManager.serviceDriver.register(new TaskedEntry(
                    PrexorCloudManager.serviceDriver.getFreePort(group.getGroupType().equalsIgnoreCase("PROXY")),
                    group.getName(),
                    group.getName() + PrexorCloudManager.config.getSplitter() + id,
                    group.getStorage().getRunningNode(),
                    PrexorCloudManager.config.isUseProtocol(),
                    id,
                    false,
                    ""
            ));
        } else if (packet instanceof PacketInServiceConnect packetCast) {
            if (PrexorCloudManager.serviceDriver.getService(packetCast.getService()) == null) {
                System.out.println("Disconnect 1 ");
                channel.disconnect();
            } else if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(packetCast.getService())) {
                System.out.println("Disconnect 2 ");
                channel.disconnect();
            } else {
                NettyDriver.getInstance().getNettyServer().registerChannel(packetCast.getService(), channel);
                TaskedEntry entry = PrexorCloudManager.serviceDriver.getService(packetCast.getService()).getEntry();
                Group group = Driver.getInstance().getGroupDriver().load(entry.getGroupName());

                Driver.getInstance().getTerminalDriver().log(
                        Type.NETWORK,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-service-connect")
                                .replace("%service%", packetCast.getService())
                );

                if (group.getGroupType().equals("PROXY")) {
                    NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutServiceConnected(packetCast.getService(), group.getName()));

                    new TimerBase().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            PrexorCloudManager.serviceDriver.getServices().forEach(taskedService -> {
                                if (taskedService.getEntry().getServiceState() != ServiceState.STARTED && taskedService.getEntry().getServiceState() != ServiceState.QUEUED) {
                                    channel.writeAndFlush(new PacketOutServiceConnected(taskedService.getEntry().getServiceName(), taskedService.getEntry().getGroupName()));
                                    NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutServiceConnected(taskedService.getEntry().getServiceName(), taskedService.getEntry().getGroupName()));
                                }
                            });
                        }
                    }, 10, TimeUtil.SECONDS);

                    Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudProxyConnectedEvent(
                            packetCast.getService(),
                            entry.getTaskNode(),
                            PrexorCloudManager.config.getNodes().stream().filter(managerConfigNodes -> managerConfigNodes.getName().equals(entry.getTaskNode())).toList().getFirst().getAddress(),
                            entry.getGroupName(),
                            entry.getUsedPort()
                    ));
                } else {
                    NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutServiceConnected(packetCast.getService(), group.getName()));

                    new TimerBase().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            PrexorCloudManager.serviceDriver.getServices().forEach(taskedService -> {
                                if (taskedService.getEntry().getServiceState() != ServiceState.STARTED && taskedService.getEntry().getServiceState() != ServiceState.QUEUED) {
                                    channel.writeAndFlush(new PacketOutServiceConnected(taskedService.getEntry().getServiceName(), taskedService.getEntry().getGroupName()));
                                    NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutServiceConnected(taskedService.getEntry().getServiceName(), taskedService.getEntry().getGroupName()));
                                }
                            });
                        }
                    }, 10, TimeUtil.SECONDS);

                    Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudServiceConnectedEvent(
                            packetCast.getService(),
                            entry.getTaskNode(),
                            PrexorCloudManager.config.getNodes().stream().filter(managerConfigNodes -> managerConfigNodes.getName().equals(entry.getTaskNode())).toList().getFirst().getAddress(),
                            entry.getGroupName(),
                            entry.getUsedPort()
                    ));
                }
            }
        } else if (packet instanceof PacketInServiceDisconnect packetCast) {
            Group group = Driver.getInstance().getGroupDriver().load(PrexorCloudManager.serviceDriver.getService(packetCast.getService()).getEntry().getGroupName());
            if (group.getGroupType().equals("PROXY"))
                Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudProxyDisconnectedEvent(packetCast.getService()));
            else
                Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudServiceDisconnectedEvent(packetCast.getService()));

            PrexorCloudManager.serviceDriver.unregister(packetCast.getService());
        } else if (packet instanceof PacketInStopService packetCast) {
            PrexorCloudManager.serviceDriver.unregister(packetCast.getService());
        } else if (packet instanceof PacketLaunchServiceWithCustomTemplate packetCast) {
            Group group = Driver.getInstance().getGroupDriver().load(packetCast.getGroup());
            String id = "";

            if (PrexorCloudManager.config.getUuid().equals("INT"))
                id = String.valueOf(PrexorCloudManager.serviceDriver.getFreeUUID(packetCast.getGroup()));
            else if (PrexorCloudManager.config.getUuid().equals("RANDOM"))
                id = PrexorCloudManager.serviceDriver.getFreeUUID();

            String serviceName = group.getName() + PrexorCloudManager.config.getSplitter() + id;
            PrexorCloudManager.serviceDriver.register(new TaskedEntry(
                    PrexorCloudManager.serviceDriver.getFreePort(group.getGroupType().equalsIgnoreCase("PROXY")),
                    group.getName(),
                    serviceName,
                    group.getStorage().getRunningNode(), PrexorCloudManager.config.isUseProtocol(), id, true, packetCast.getTemplate()
            ));
        } else if (packet instanceof PacketInChangeState packetCast) {
            PrexorCloudManager.serviceDriver.getService(packetCast.getService()).handleStatusChange(ServiceState.valueOf(packetCast.getState()));
        }
    }

}
