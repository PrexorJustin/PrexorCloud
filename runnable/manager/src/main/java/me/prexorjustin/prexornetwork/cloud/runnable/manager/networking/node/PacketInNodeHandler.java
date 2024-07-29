package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.node;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.authentication.AuthenticatorKey;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.liveservice.LiveServices;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node.*;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.node.PacketOutAuthSuccess;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.cloudservices.entry.TaskedEntry;

public class PacketInNodeHandler implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInAuthNode packetCast) {
            AuthenticatorKey authConfig = (AuthenticatorKey) new ConfigDriver("./connection.key").read(AuthenticatorKey.class);
            Driver.getInstance().getTerminalDriver().log(
                    Type.NETWORK,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-auth-request")
                            .replace("%node%", packetCast.getNode())
            );

            if (PrexorCloudManager.config.getNodes().parallelStream().noneMatch(managerConfigNodes -> managerConfigNodes.getName().equals(packetCast.getNode()))) {
                Driver.getInstance().getTerminalDriver().log(Type.NETWORK, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-auth-request-not-found"));
                channel.disconnect();
            } else if (!Driver.getInstance().getMessageStorage().base64ToUTF8(authConfig.getKey()).equals(packetCast.getKey())) {
                Driver.getInstance().getTerminalDriver().log(Type.NETWORK, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-auth-request-key"));
                channel.disconnect();
            } else if (NettyDriver.getInstance().getNettyServer().isChannelRegistered(packetCast.getNode())) {
                Driver.getInstance().getTerminalDriver().log(Type.NETWORK, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-auth-request-already-connected"));
                channel.disconnect();
            } else {
                NettyDriver.getInstance().getNettyServer().registerChannel(packetCast.getNode(), channel);
                NettyDriver.getInstance().getNettyServer().sendPacketAsynchronous(packetCast.getNode(), new PacketOutAuthSuccess());

                Driver.getInstance().getTerminalDriver().log(Type.NETWORK,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-auth-request-successful")
                                .replace("%node%", packetCast.getNode())
                );
            }
        } else if (packet instanceof PacketInNodeActionSuccess packetCast) {
            if (packetCast.isLaunched()) {
                TaskedEntry entry = PrexorCloudManager.serviceDriver.getService(packetCast.getService()).getEntry();
                entry.setUsedPort(packetCast.getPort());

                LiveServices liveServices = (LiveServices) new ConfigDriver().convert(PrexorCloudManager.restDriver.get("/services/" + entry.getServiceName().replace(PrexorCloudManager.config.getSplitter(), "~")), LiveServices.class);
                liveServices.setPort(packetCast.getPort());

                Driver.getInstance().getWebServer().updateRoute("/services/" + entry.getServiceName().replace(PrexorCloudManager.config.getSplitter(), "~"), new ConfigDriver().convert(liveServices));

                Driver.getInstance().getTerminalDriver().log(Type.INFO,
                        Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-start")
                                .replace("%service%", packetCast.getService())
                                .replace("%node%", packetCast.getNode())
                                .replace("%port%", "" + entry.getUsedPort())
                );
                PrexorCloudManager.serviceDriver.getService(packetCast.getService()).getEntry().setUsedPort(packetCast.getPort());
            } else {
                PrexorCloudManager.serviceDriver.unregister(packetCast.getService());
            }
        } else if (packet instanceof PacketInShutdownNode packetCast) {
            PrexorCloudManager.serviceDriver.getServicesFromNode(packetCast.getNode()).forEach(taskedService -> PrexorCloudManager.serviceDriver.unregister(taskedService.getEntry().getServiceName()));

            if (Driver.getInstance().getMessageStorage().getScreenForm().equalsIgnoreCase(packetCast.getNode()))
                PrexorCloudManager.screenNode(packetCast.getNode());

            NettyDriver.getInstance().getNettyServer().removeChannel(packetCast.getNode());
            Driver.getInstance().getTerminalDriver().log(Type.INFO,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("network-node-stop")
                            .replace("%node%", packetCast.getNode())
            );
        } else if (packet instanceof PacketInSendConsole packetCast) {
            Driver.getInstance().getTerminalDriver().log(packetCast.getService(), packetCast.getLine());
        } else if (packet instanceof PacketInSendConsoleFromNode packetCast) {
            Driver.getInstance().getTerminalDriver().log(Driver.getInstance().getMessageStorage().getScreenForm(), packetCast.getLine());
        }
    }
}
