package me.prexorjustin.prexornetwork.cloud.plugin.bukkit.listener;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.addresses.Addresses;
import me.prexorjustin.prexornetwork.cloud.plugin.bukkit.BukkitBootstrap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ServiceListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Messages messageConfig = CloudAPI.getInstance().getMessageConfig();
        Group group = CloudAPI.getInstance().getGroupPool().getGroup(BukkitBootstrap.getInstance().getService().getGroup());
        if (group.isMaintenance() && !event.getPlayer().hasPermission("prexorcloud.maintenance.bypass"))
            event.getPlayer().kickPlayer(
                    messageConfig.getMessages().get("connectingGroupMaintenance")
                            .replace("&", "§")
                            .replace("%PREFIX%", messageConfig.getMessages().get("prefix").replace("&", "§"))
            );
        else if (!event.getPlayer().hasPermission(group.getPermission()))
            event.getPlayer().kickPlayer(
                    messageConfig.getMessages().get("noPermsToJoinTheService")
                            .replace("&", "§")
                            .replace("%PREFIX%", messageConfig.getMessages().get("prefix").replace("&", "§"))
            );

        Addresses addresses = (Addresses) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/default/addresses"), Addresses.class);
        if (addresses.getAddresses().stream().noneMatch(s -> s.equalsIgnoreCase(event.getAddress().getHostAddress())) || CloudAPI.getInstance().getPlayerPool().isPlayerNull(event.getPlayer().getName()))
            event.getPlayer().kickPlayer(messageConfig.getMessages().get("kickOnlyProxyJoin").replace("&", "§"));
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        Addresses addresses = (Addresses) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/default/addresses"), Addresses.class);
        if (addresses.getAddresses().stream().noneMatch(s -> s.equalsIgnoreCase(event.getAddress().getHostAddress()))) {
            event.setMaxPlayers(0);
            event.setServerIcon(null);
        }
    }
}
