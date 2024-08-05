package me.prexorjustin.prexornetwork.cloud.runnable.manager.networking.command;

import io.netty.channel.Channel;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.whitelist.Whitelist;
import me.prexorjustin.prexornetwork.cloud.networking.packet.NettyAdaptor;
import me.prexorjustin.prexornetwork.cloud.networking.packet.Packet;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandMaintenance;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandMaxPlayers;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandMinCount;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.service.command.PacketInCommandWhitelist;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

public class HandlePacketInCommand implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInCommandMaintenance packetCast) {
            if (Driver.getInstance().getGroupDriver().find(packetCast.getName())) {
                Group raw = Driver.getInstance().getGroupDriver().load(((PacketInCommandMaintenance) packet).getName());
                raw.setMaintenance(packetCast.isRemoved());

                Driver.getInstance().getGroupDriver().update(raw);
                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
            }
        } else if (packet instanceof PacketInCommandMaxPlayers packetCast) {
            if (Driver.getInstance().getGroupDriver().find(packetCast.getGroup())) {
                Group raw = Driver.getInstance().getGroupDriver().load(packetCast.getGroup());
                raw.setMaxPlayer(packetCast.getAmount());

                Driver.getInstance().getGroupDriver().update(raw);
                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
            }
        } else if (packet instanceof PacketInCommandMinCount packetCast) {
            if (Driver.getInstance().getGroupDriver().find(packetCast.getGroup())) {
                Group raw = Driver.getInstance().getGroupDriver().load(packetCast.getGroup());
                raw.setMinOnline(packetCast.getAmount());

                Driver.getInstance().getGroupDriver().update(raw);
                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + raw.getName(), new ConfigDriver().convert(raw));
            }
        } else if (packet instanceof PacketInCommandWhitelist packetCast) {
            if (!PrexorCloudManager.config.getWhitelist().contains(packetCast.getName()))
                PrexorCloudManager.config.getWhitelist().add(packetCast.getName());
            else
                PrexorCloudManager.config.getWhitelist().remove(packetCast.getName());

            new ConfigDriver("./service.json").save(PrexorCloudManager.config);
            Whitelist whitelistConfig = new Whitelist();
            whitelistConfig.setWhitelist(PrexorCloudManager.config.getWhitelist());
            Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.WHITELIST.getRoute(), new ConfigDriver().convert(whitelistConfig));
        }
    }
}
